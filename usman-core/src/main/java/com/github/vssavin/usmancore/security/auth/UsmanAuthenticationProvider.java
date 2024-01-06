package com.github.vssavin.usmancore.security.auth;

import com.github.vssavin.usmancore.auth.UsmanBaseAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * An {@link org.springframework.security.authentication.AuthenticationProvider}
 * implementation that attempts to authenticate using corresponding authentication
 * service.
 *
 * @author vssavin on 11.12.2023.
 */
@Component
public class UsmanAuthenticationProvider implements AuthenticationProvider {

    private final UsmanBaseAuthenticator usmanBaseAuthenticator;

    @Autowired
    public UsmanAuthenticationProvider(UsmanBaseAuthenticator usmanBaseAuthenticator) {
        this.usmanBaseAuthenticator = usmanBaseAuthenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return usmanBaseAuthenticator.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(usmanBaseAuthenticator.authenticationClass());
    }

}
