package com.github.vssavin.usmancore.config;

import static com.github.vssavin.usmancore.config.Role.ROLE_ADMIN;
import static com.github.vssavin.usmancore.config.Role.ROLE_USER;

/**
 * Enum with available permissions.
 *
 * @author vssavin on 28.11.2023
 */
public enum Permission {

	USER_ADMIN(Role.getStringRole(ROLE_USER), Role.getStringRole(ROLE_ADMIN)),
	ADMIN_ONLY(Role.getStringRole(ROLE_ADMIN)), ANY_USER();

	private final String[] roles;

	Permission(String... roles) {
		this.roles = roles;
	}

	public String[] getRoles() {
		return roles;
	}

}
