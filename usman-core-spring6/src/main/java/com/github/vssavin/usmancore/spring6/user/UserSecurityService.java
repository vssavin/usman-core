package com.github.vssavin.usmancore.spring6.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
 * Main interface for checking user authorization.
 *
 * @author vssavin on 19.12.2023.
 */
@Service
public interface UserSecurityService {

    String getAuthorizedUserName(HttpServletRequest request);

    String getAuthorizedUserLogin(HttpServletRequest request);

    boolean isAuthorizedAdmin(HttpServletRequest request);

    boolean isAuthorizedUser(HttpServletRequest request);

}
