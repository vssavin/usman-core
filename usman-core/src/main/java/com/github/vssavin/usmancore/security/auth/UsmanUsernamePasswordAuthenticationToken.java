package com.github.vssavin.usmancore.security.auth;

import com.github.vssavin.jcrypt.DefaultStringSafety;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Provides erasing string credentials.
 *
 * @author vssavin on 11.12.2023.
 */
public class UsmanUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public UsmanUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UsmanUsernamePasswordAuthenticationToken(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    @Override
    public void eraseCredentials() {
        Object credentials = super.getCredentials();
        if (credentials instanceof String) {
            new DefaultStringSafety().clearString((String) credentials);
        }
        super.eraseCredentials();
    }

}
