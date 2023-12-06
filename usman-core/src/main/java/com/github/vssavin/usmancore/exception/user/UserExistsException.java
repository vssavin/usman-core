package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that the specified user exists.
 *
 * @author vssavin on 01.12.2023.
 */
public class UserExistsException extends RuntimeException {

    public UserExistsException(String message) {
        super(message);
    }

    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
