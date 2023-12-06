package com.github.vssavin.usmancore.exception.auth;

/**
 * Special unchecked exception type used to indicate that authentication procedure is
 * forbidden.
 *
 * @author vssavin on 01.12.2023.
 */
public class AuthenticationForbiddenException extends RuntimeException {

    public AuthenticationForbiddenException(String message) {
        super(message);
    }

}
