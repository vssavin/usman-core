package com.github.vssavin.usmancore.spring6.auth;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import com.github.vssavin.usmancore.auth.UsmanBaseAuthenticationService;
import com.github.vssavin.usmancore.config.ArgumentsProcessedNotifier;
import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.config.UsmanSecureServiceArgumentsHandler;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.exception.auth.AuthenticationForbiddenException;
import com.github.vssavin.usmancore.exception.user.UserExpiredException;
import com.github.vssavin.usmancore.exception.user.UserNotFoundException;
import com.github.vssavin.usmancore.spring6.event.EventService;
import com.github.vssavin.usmancore.spring6.user.User;
import com.github.vssavin.usmancore.spring6.user.UserService;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link com.github.vssavin.usmancore.spring6.auth.AuthService} implementation that:
 * 1. Attempts to authenticate the user using o2Auth or user/password mechanism. 2.
 * Handling success authentication and creating the corresponding user event. 3. Handling
 * failed authentication adds the current IP address to the blacklist. 4. Checks if
 * authentication is allowed for the specified ip address uses a blacklist.
 *
 * @author vssavin on 11.12.2023.
 */
@Service
public class UsmanAuthService extends UsmanBaseAuthenticationService
        implements AuthService, ArgumentsProcessedNotifier {

    private static final Logger log = LoggerFactory.getLogger(UsmanAuthService.class);

    private final UserService userService;

    private final EventService eventService;

    private final UsmanConfigurer usmanConfigurer;

    @Autowired
    public UsmanAuthService(UserService userService, EventService eventService, UsmanConfigurer usmanConfigurer,
            PasswordEncoder passwordEncoder) {
        super(userService, passwordEncoder, usmanConfigurer.getSecureService(), usmanConfigurer);
        this.userService = userService;
        this.eventService = eventService;
        this.usmanConfigurer = usmanConfigurer;
    }

    @Override
    @Transactional
    @UsmanRouteDatasource
    public Collection<GrantedAuthority> processSuccessAuthentication(Authentication authentication,
            HttpServletRequest request, EventType eventType) {

        User user = null;
        if (eventType == EventType.LOGGED_OUT) {
            Optional<Cookie> cookieOptional = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("remember-me"))
                .findFirst();
            if (cookieOptional.isPresent()) {
                String cookieValue = cookieOptional.get().getValue();
                String userLogin = decodeCookie(cookieValue)[0];
                user = userService.getUserByLogin(userLogin);
            }

        }
        else {
            if (authentication == null || authentication.getPrincipal() == null) {
                return Collections.emptyList();
            }

            try {
                OAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
                user = userService.processOAuthPostLogin(oAuth2User);
            }
            catch (ClassCastException e) {
                // ignore, it's ok
            }

            if (user == null && authentication.getPrincipal() instanceof User) {
                user = userService.getUserByLogin(((User) authentication.getPrincipal()).getLogin());
            }

            if (user == null) {
                user = userService.getUserByLogin(authentication.getPrincipal().toString());
            }

            if (user == null) {
                throw new UserNotFoundException(
                        String.format("User [%s] not found!", authentication.getPrincipal().toString()));
            }

            if (user.getExpirationDate().before(new Date())) {
                userService.deleteUser(user);
                throw new UserExpiredException(String.format("User [%s] has been expired!", user.getLogin()));
            }
        }

        if (user == null) {
            String userLogin = authentication.getPrincipal().toString();
            throw new AuthenticationForbiddenException("User " + userLogin + " not found!");
        }

        saveUserEvent(user, request, eventType);
        return user.getAuthorities()
            .stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
            .collect(Collectors.toList());
    }

    @Override
    public boolean isAuthenticationAllowed(String ipAddress) {
        int failureCounts = getFailureCount(ipAddress);
        if (failureCounts >= maxFailureCount) {
            long expireTime = getBanExpirationTime(ipAddress);
            return expireTime <= 0 || System.currentTimeMillis() >= expireTime;
        }
        return true;
    }

    @Override
    public void processFailureAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) {
        String userIp = request.getRemoteAddr();
        int failureCount = getFailureCount(userIp);

        if (failureCount >= maxFailureCount) {
            long expirationTime = getBanExpirationTime(userIp);
            if (expirationTime == 0) {
                blockIp(userIp);
                log.info("IP {} has been blocked!", userIp);
                throw new AuthenticationForbiddenException("Sorry! You have been blocked! Try again later!");
            }
            else if (expirationTime < System.currentTimeMillis()) {
                resetFailureCount(userIp);
                incrementFailureCount(userIp);
            }
            else {
                throw new AuthenticationForbiddenException("Sorry! You have been blocked! Try again later!");
            }
        }
        else {
            incrementFailureCount(userIp);
        }
    }

    @Override
    public void notifyArgumentsProcessed(Class<?> aClass) {
        if (aClass != null && UsmanSecureServiceArgumentsHandler.class.isAssignableFrom(aClass)) {
            super.setSecureService(usmanConfigurer.getSecureService());
        }
    }

    private String[] decodeCookie(String cookieValue) {
        String cookieAsPlainText = new String(Base64.getDecoder().decode(cookieValue.getBytes()));
        return StringUtils.delimitedListToStringArray(cookieAsPlainText, ":");
    }

    private void saveUserEvent(User user, HttpServletRequest request, EventType eventType) {
        String message = "";
        switch (eventType) {
            case LOGGED_IN:
                message = String.format("User [%s] logged in using IP: %s", user.getLogin(), request.getRemoteAddr());
                break;
            case LOGGED_OUT:
                message = String.format("User [%s] logged out using IP: %s", user.getLogin(), request.getRemoteAddr());
                break;
        }
        eventService.createEvent(user, eventType, message);
    }

}
