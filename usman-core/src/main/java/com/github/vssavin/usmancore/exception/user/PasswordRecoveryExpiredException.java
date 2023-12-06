package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that password recovery failed.
 *
 * @author vssavin on 01.12.2023.
 */
public class PasswordRecoveryExpiredException extends RuntimeException {

    public PasswordRecoveryExpiredException(String message) {
        super(message);
    }

    public PasswordRecoveryExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}
