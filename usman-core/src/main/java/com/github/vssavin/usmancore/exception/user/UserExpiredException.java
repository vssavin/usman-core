package com.github.vssavin.usmancore.exception.user;

/**
 * Special unchecked exception type used to indicate that the specified user has been
 * expired.
 *
 * @author vssavin on 01.12.2023.
 */
public class UserExpiredException extends RuntimeException {

	public UserExpiredException(String message) {
		super(message);
	}

}
