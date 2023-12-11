package com.github.vssavin.usmancore.spring6.security.rememberme;

import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Represents methods for handling authentication.
 *
 * @author vssavin on 11.12.2023.
 */
public interface Authenticator {

    Authentication retrieveAuthentication(HttpServletRequest request, HttpServletResponse response);

}
