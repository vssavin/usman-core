package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that an error has occurred in a user
 * service.
 *
 * @author vssavin on 01.12.2023.
 */
public class UserServiceException extends RuntimeException {

	public UserServiceException(String msg, Throwable e) {
		super(msg, e);
	}

	public UserServiceException(String message) {
		super(message);
	}

}
