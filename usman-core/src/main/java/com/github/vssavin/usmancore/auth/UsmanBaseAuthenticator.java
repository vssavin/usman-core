package com.github.vssavin.usmancore.auth;

import org.springframework.security.core.Authentication;

/**
 * Main authentication interface.
 *
 * @author vssavin on 11.12.2023.
 */
public interface UsmanBaseAuthenticator {

    Authentication authenticate(Authentication authentication);

    boolean isAuthenticationAllowed(String authId);

    Class<? extends Authentication> authenticationClass();

}
