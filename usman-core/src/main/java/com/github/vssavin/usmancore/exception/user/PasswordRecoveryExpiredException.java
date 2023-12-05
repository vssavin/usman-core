package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that password recovery failed.
 *
 * @author vssavin on 01.12.2023.
 */
public class PasswordRecoveryExpiredException extends RuntimeException {

	PasswordRecoveryExpiredException(String message) {
		super(message);
	}

	PasswordRecoveryExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

}
