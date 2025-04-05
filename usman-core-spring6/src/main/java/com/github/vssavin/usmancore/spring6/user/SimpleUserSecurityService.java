package com.github.vssavin.usmancore.spring6.user;

import com.github.vssavin.usmancore.config.Role;
import com.github.vssavin.usmancore.exception.user.EmailNotFoundException;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.github.vssavin.usmancore.user.UsmanUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Service for checking user authorization.
 *
 * @author vssavin on 19.12.2023.
 */
@Service
public class SimpleUserSecurityService implements UserSecurityService {

    private final UsmanUserService userService;

    @Autowired
    public SimpleUserSecurityService(UsmanUserService userService) {
        this.userService = userService;
    }

    @Override
    public String getAuthorizedUserName(HttpServletRequest request) {
        String authorizedUserName = getAuthorizedUserLogin(request);
        String userLogin = authorizedUserName;
        if (!authorizedUserName.isEmpty()) {
            try {
                authorizedUserName = userService.getUserByLogin(authorizedUserName).getName();
            }
            catch (UsernameNotFoundException e) {
                authorizedUserName = "";
            }
            if (authorizedUserName.isEmpty()) {
                try {
                    authorizedUserName = userService.getUserByEmail(authorizedUserName).getName();
                }
                catch (EmailNotFoundException ignore) {
                    // ignore, it's ok
                }
            }
        }
        if (authorizedUserName.isEmpty()) {
            throw new UsernameNotFoundException("User: " + userLogin + " not found!");
        }
        return authorizedUserName;
    }

    @Override
    public String getAuthorizedUserLogin(HttpServletRequest request) {
        UsmanUser user;
        try {
            user = getAuthorizedUser(request);
        }
        catch (UsernameNotFoundException e) {
            return "";
        }
        return user.getLogin();
    }

    @Override
    public boolean isAuthorizedAdmin(HttpServletRequest request) {
        UsmanUser user = null;
        try {
            user = getAuthorizedUser(request);
        }
        catch (UsernameNotFoundException ignore) {
            // ignore, it's ok
        }
        return user != null && Role.getRole(user.getAuthority()) == Role.ROLE_ADMIN;
    }

    @Override
    public boolean isAuthorizedUser(HttpServletRequest request) {
        UsmanUser user = null;
        try {
            user = getAuthorizedUser(request);
        }
        catch (UsernameNotFoundException ignore) {
            // ignore, it's ok
        }

        return user != null && Role.getRole(user.getAuthority()) == Role.ROLE_USER;
    }

    private UsmanUser getAuthorizedUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        UsmanUser user = null;
        if (principal != null) {
            if (principal instanceof OAuth2AuthenticationToken) {
                user = userService.getUserByOAuth2Token((OAuth2AuthenticationToken) principal);
            }
            else {
                user = userService.getUserByLogin(principal.getName());
            }
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        return user;
    }

}
