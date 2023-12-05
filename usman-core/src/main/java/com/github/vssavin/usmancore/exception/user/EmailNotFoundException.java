package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that email is not found.
 *
 * @author vssavin on 01.12.2023.
 */
public class EmailNotFoundException extends RuntimeException {

	public EmailNotFoundException(String message) {
		super(message);
	}

	public EmailNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
