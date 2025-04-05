package com.github.vssavin.usmancore.user;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.data.pagination.Paged;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Main interface for service provides user management.
 *
 * @author vssavin on 05.04.2025.
 */
@Service
public interface UsmanUserService extends UserDetailsService {

    Paged<UsmanUser> getUsers(UserFilter userFilter, int pageNumber, int size);

    UsmanUser getUserById(Long id);

    UsmanUser addUser(UsmanUser user);

    UsmanUser updateUser(UsmanUser user);

    UsmanUser getUserByName(String name);

    UsmanUser getUserByLogin(String login);

    UsmanUser getUserByEmail(String email);

    void deleteUser(UsmanUser user);

    UsmanUser registerUser(String login, String username, String password, String email, Role role);

    void confirmUser(String login, String verificationId, boolean isAdminUser);

    String generateNewUserPassword(String recoveryId);

    Map<String, UsmanUser> getUserRecoveryId(String loginOrEmail);

    UsmanUser getUserByRecoveryId(String recoveryId);

    boolean accessGrantedForRegistration(Role role, String authorizedName);

    UsmanUser processOAuthPostLogin(OAuth2User oAuth2User);

    UsmanUser getUserByOAuth2Token(OAuth2AuthenticationToken token);

    Predicate userFilterToPredicate(UserFilter userFilter);

    BooleanExpression processAndEqualLong(BooleanExpression expression, SimpleExpression<Long> simpleExpression,
            Long value);

    BooleanExpression processAndLikeString(BooleanExpression expression, StringExpression stringExpression,
            String value);

}
