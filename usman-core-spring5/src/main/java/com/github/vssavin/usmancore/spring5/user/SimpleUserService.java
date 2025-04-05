package com.github.vssavin.usmancore.spring5.user;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.data.pagination.Paging;
import com.github.vssavin.usmancore.exception.user.*;
import com.github.vssavin.usmancore.user.UserFilter;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.github.vssavin.usmancore.user.UsmanUserService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main implementation of user management service.
 *
 * @author vssavin on 07.12.2023.
 */
@Service
public class SimpleUserService implements UsmanUserService {

    private static final Map<String, UserRecoveryParams> passwordRecoveryIds = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Autowired
    public SimpleUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @UsmanRouteDatasource
    @Override
    public Paged<UsmanUser> getUsers(UserFilter userFilter, int pageNumber, int size) {
        String errorMessage = String.format(
                "Error while search user with params: pageNumber = %d, size = %d, filter: [%s]!", pageNumber, size,
                userFilter);
        Page<UsmanUser> users;
        Pageable pageable;
        try {
            pageable = PageRequest.of(pageNumber - 1, size);
            if (userFilter == null || userFilter.isEmpty()) {
                users = userRepository.findAll(pageable).map(usmanUser -> usmanUser);
            }
            else {
                Predicate predicate = userFilterToPredicate(userFilter);
                users = userRepository.findAll(predicate, pageable).map(usmanUser -> usmanUser);
            }

            return new Paged<>(users, Paging.of(users.getTotalPages(), pageNumber, size));
        }
        catch (Exception e) {
            throw new UserServiceException(errorMessage, e);
        }
    }

    @UsmanRouteDatasource
    @Override
    public User getUserById(Long id) {
        Optional<User> user;
        try {
            user = userRepository.findById(id);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Getting a user by id = %d error!", id), e);
        }

        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User with id = %d not found!", id));
        }

        return user.get();
    }

    @UsmanRouteDatasource
    @Override
    public User addUser(UsmanUser user) {
        try {
            return userRepository.save((User) user);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Adding error for user [%s]!", user), e);
        }
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser updateUser(UsmanUser user) {
        try {
            return userRepository.save((User) user);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Update error for user [%s]", user), e);
        }
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser getUserByName(String name) {
        try {
            List<User> users = userRepository.findUserByName(name);
            if (!users.isEmpty()) {
                return users.get(0);
            }
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Error while getting user by name [%s]", name), e);
        }

        throw new UsernameNotFoundException(String.format("User: %s not found!", name));
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser getUserByLogin(String login) {
        List<User> users;
        try {
            users = userRepository.findByLogin(login);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Error while getting user by login [%s]", login), e);
        }
        if (!users.isEmpty()) {
            return users.get(0);
        }
        throw new UsernameNotFoundException(String.format("User with login: %s not found!", login));

    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser getUserByEmail(String email) {
        List<User> users;
        try {
            users = userRepository.findByEmail(email);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Error while getting user by email [%s]", email), e);
        }

        if (!users.isEmpty()) {
            return users.get(0);
        }
        throw new EmailNotFoundException(String.format("Email: %s not found!", email));
    }

    @UsmanRouteDatasource
    @Override
    public void deleteUser(UsmanUser user) {
        Objects.requireNonNull(user, "User must not be null!");
        try {
            userRepository.deleteByLogin(user.getLogin());
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("Error while deleting user [%s]", user), e);
        }
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser registerUser(String login, String username, String password, String email, Role role) {
        UsmanUser user = null;
        try {
            user = getUserByLogin(login);
        }
        catch (UsernameNotFoundException e) {
            // ignore
        }

        if (user != null) {
            throw new UserExistsException(String.format("User %s already exists!", username));
        }

        user = new User(login, username, password, email, role.name());
        try {
            return addUser(user);
        }
        catch (Exception e) {
            throw new UserServiceException(String.format("User [%s] registration error!", user), e);
        }
    }

    @UsmanRouteDatasource
    @Override
    public void confirmUser(String login, String verificationId, boolean isAdminUser) {
        UsmanUser user = null;
        try {
            user = getUserByLogin(login);
        }
        catch (UsernameNotFoundException e) {
            // ignore
        }

        if (isAdminUser && (verificationId == null || verificationId.isEmpty()) && user != null) {
            verificationId = user.getVerificationId();
        }

        if (user != null && user.getVerificationId().equals(verificationId)) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            Date userExpirationDate = user.getExpirationDate();
            long maxExpirationMs = (long) User.EXPIRATION_DAYS * 86_400_000;
            if (currentDate.after(userExpirationDate)
                    || Math.abs(currentDate.getTime() - userExpirationDate.getTime()) < maxExpirationMs) {
                calendar.add(Calendar.YEAR, 100);
                user.setExpirationDate(calendar.getTime());
                try {
                    updateUser(user);
                }
                catch (Exception e) {
                    throw new UserConfirmFailedException(e.getMessage(), e);
                }
            }
        }
        else {
            throw new UserConfirmFailedException("Undefined user verificationId!");
        }
    }

    @Override
    public String generateNewUserPassword(String recoveryId) {
        UserRecoveryParams userRecoveryParams = passwordRecoveryIds.get(recoveryId);
        if (userRecoveryParams.getExpirationTime().isAfter(LocalDateTime.now())) {
            String newPassword = generateRandomPassword(15);
            userRecoveryParams.getUser().setPassword(passwordEncoder.encode(newPassword));
            return newPassword;
        }
        else {
            throw new PasswordRecoveryExpiredException("Recovery id " + "[" + recoveryId + "] is expired");
        }
    }

    @UsmanRouteDatasource
    @Override
    public Map<String, UsmanUser> getUserRecoveryId(String loginOrEmail) {
        String errorMessage = String.format("Error while getting recovery id, login/email = [%s]", loginOrEmail);
        User user = findUserByLoginOrEmail(loginOrEmail, errorMessage);

        UserRecoveryParams userRecoveryParams = new UserRecoveryParams(user);
        passwordRecoveryIds.put(userRecoveryParams.getRecoveryId(), userRecoveryParams);
        return Collections.singletonMap(userRecoveryParams.getRecoveryId(), userRecoveryParams.getUser());
    }

    @Override
    public User getUserByRecoveryId(String recoveryId) {
        UserRecoveryParams userRecoveryParams = passwordRecoveryIds.get(recoveryId);
        if (userRecoveryParams == null) {
            throw new UserServiceException("User with recoveryId = " + recoveryId + " not found!");
        }
        return userRecoveryParams.getUser();
    }

    @Override
    public boolean accessGrantedForRegistration(Role role, String authorizedName) {
        boolean granted = true;

        if (role.equals(Role.ROLE_ADMIN)) {
            if (authorizedName != null && !authorizedName.isEmpty()) {
                try {
                    UsmanUser admin = getUserByLogin(authorizedName);
                    if (!Role.ROLE_ADMIN.name().equals(admin.getAuthority())) {
                        granted = false;
                    }
                }
                catch (UsernameNotFoundException e) {
                    granted = false;
                }
            }
            else {
                granted = false;
            }
        }

        return granted;
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser processOAuthPostLogin(OAuth2User oAuth2User) {
        UsmanUser user = null;
        String email = oAuth2User.getAttribute("email");
        try {
            user = getUserByEmail(email);
        }
        catch (EmailNotFoundException e) {
            // ignore, it's ok
        }

        if (user == null) {
            user = registerUser(email, email, generateRandomPassword(10), email, Role.ROLE_USER);
            confirmUser(user.getLogin(), user.getVerificationId(), true);
        }

        return user;
    }

    @UsmanRouteDatasource
    @Override
    public UsmanUser getUserByOAuth2Token(OAuth2AuthenticationToken token) {
        OAuth2User oAuth2User = token.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        try {
            return getUserByEmail(email);
        }
        catch (EmailNotFoundException e) {
            return null;
        }
    }

    @UsmanRouteDatasource
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByLogin(username);
    }

    @Override
    public Predicate userFilterToPredicate(UserFilter userFilter) {
        BooleanExpression expression = null;
        QUser user = QUser.user;
        expression = processAndEqualLong(expression, user.id, userFilter.getUserId());
        expression = processAndLikeString(expression, user.email, userFilter.getEmail());
        expression = processAndLikeString(expression, user.name, userFilter.getName());
        expression = processAndLikeString(expression, user.login, userFilter.getLogin());
        return expression;
    }

    @Override
    public BooleanExpression processAndEqualLong(BooleanExpression expression, SimpleExpression<Long> simpleExpression,
            Long value) {
        if (value != null) {
            if (expression != null) {
                expression = expression.and(simpleExpression.eq(value));
            }
            else {
                expression = simpleExpression.eq(value);
            }
        }

        return expression;
    }

    @Override
    public BooleanExpression processAndLikeString(BooleanExpression expression, StringExpression stringExpression,
            String value) {
        if (value != null && !value.isEmpty()) {
            if (expression != null) {
                expression = expression.and(stringExpression.like(value));
            }
            else {
                expression = stringExpression.like(value);
            }
        }

        return expression;
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, length)
            .map(i -> random.nextInt(chars.length()))
            .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
            .collect(Collectors.joining());
    }

    private User findUserByLoginOrEmail(String loginOrEmail, String errorMessage) {
        List<User> users;

        try {
            users = userRepository.findByEmail(loginOrEmail);
        }
        catch (Exception e) {
            throw new UserServiceException(errorMessage, e);
        }

        if (users.isEmpty()) {
            try {
                users = userRepository.findByLogin(loginOrEmail);
            }
            catch (Exception e) {
                throw new UserServiceException(errorMessage, e);
            }

            if (users.isEmpty()) {
                throw new UserServiceException(String.format("User [%s] not found!", loginOrEmail));
            }
        }

        return users.get(0);
    }

    private static final class UserRecoveryParams {

        private final User user;

        private final String recoveryId;

        private final LocalDateTime expirationTime;

        private UserRecoveryParams(User user) {
            this.user = user;
            this.recoveryId = UUID.randomUUID().toString();
            this.expirationTime = LocalDateTime.now().plusDays(1);
        }

        public User getUser() {
            return user;
        }

        public String getRecoveryId() {
            return recoveryId;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

    }

}