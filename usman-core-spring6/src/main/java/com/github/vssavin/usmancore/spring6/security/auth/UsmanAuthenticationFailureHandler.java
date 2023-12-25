package com.github.vssavin.usmancore.spring6.security.auth;

import com.github.vssavin.usmancore.config.UsmanUrlsConfigurer;
import com.github.vssavin.usmancore.exception.auth.AuthenticationForbiddenException;
import com.github.vssavin.usmancore.spring6.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An {@link org.springframework.security.web.authentication.AuthenticationFailureHandler}
 * implementation that handles failed authentication using corresponding authentication
 * service.
 *
 * @author vssavin on 11.12.2023.
 */
@Component
class UsmanAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String failureRedirectUrl;

    private final AuthService authService;

    @Autowired
    UsmanAuthenticationFailureHandler(AuthService authService, UsmanUrlsConfigurer usmanUrlsConfigurer) {
        this.authService = authService;
        this.failureRedirectUrl = usmanUrlsConfigurer.getLoginUrl() + "?error=true";
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String lang = request.getParameter("lang");
        if (lang != null) {
            lang = "&lang=" + lang;
        }
        else {
            lang = "";
        }

        try {
            authService.processFailureAuthentication(request, response, exception);
        }
        catch (AuthenticationForbiddenException e) {
            response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
            return;
        }

        response.sendRedirect(failureRedirectUrl + lang);

    }

}
