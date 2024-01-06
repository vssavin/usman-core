package com.github.vssavin.usmancore.spring5.security.rememberme;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Represents methods for handling authentication.
 *
 * @author vssavin on 11.12.2023.
 */
public interface Authenticator {

    Authentication retrieveAuthentication(HttpServletRequest request, HttpServletResponse response);

}
