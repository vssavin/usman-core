package com.github.vssavin.usmancore.spring5.security.auth;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.exception.user.UserExpiredException;
import com.github.vssavin.usmancore.spring5.auth.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * An {@link org.springframework.security.web.authentication.AuthenticationSuccessHandler}
 * implementation that attempts to authenticate using corresponding authentication
 * service.
 *
 * @author vssavin on 11.12.2023.
 */
@Component
public class UsmanAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    private final UsmanUrlsConfigurer usmanUrlsConfigurer;

    public UsmanAuthenticationSuccessHandler(AuthService authService, UsmanUrlsConfigurer usmanUrlsConfigurer) {
        this.authService = authService;
        this.usmanUrlsConfigurer = usmanUrlsConfigurer;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        String successUrl = usmanUrlsConfigurer.getSuccessUrl();

        Collection<GrantedAuthority> authorities = Collections.emptyList();
        try {
            authorities = authService.processSuccessAuthentication(authentication, request, EventType.LOGGED_IN);
        }
        catch (UserExpiredException e) {
            successUrl = usmanUrlsConfigurer.getLoginUrl() + "?error=true";
        }

        if (authorities.stream().anyMatch(authority -> authority.getAuthority().equals(Role.ROLE_ADMIN.name()))) {
            successUrl = usmanUrlsConfigurer.getAdminSuccessUrl();
        }

        String lang = request.getParameter("lang");
        String delimiter = "?";
        if (successUrl.contains("?")) {
            delimiter = "&";
        }
        if (lang != null) {
            lang = delimiter + "lang=" + lang;
        }
        else {
            lang = "";
        }

        response.sendRedirect(successUrl + lang);
    }

}
