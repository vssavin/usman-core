package com.github.vssavin.usmancore.spring5.auth;

import com.github.vssavin.usmancore.auth.UsmanBaseAuthenticator;
import com.github.vssavin.usmancore.event.EventType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * The main javax-depend service interface provides user authentication.
 *
 * @author vssavin on 11.12.2023.
 */
public interface AuthService extends UsmanBaseAuthenticator {

    Collection<GrantedAuthority> processSuccessAuthentication(Authentication authentication, HttpServletRequest request,
            EventType eventType);

    void processFailureAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception);

}
