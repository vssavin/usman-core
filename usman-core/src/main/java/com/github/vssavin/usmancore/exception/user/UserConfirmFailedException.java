package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that confirmation of password failed.
 *
 * @author vssavin on 01.12.2023.
 */
public class UserConfirmFailedException extends RuntimeException {

    public UserConfirmFailedException(String message) {
        super(message);
    }

    public UserConfirmFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
