package com.github.vssavin.usmancore.config;

import com.github.vssavin.usmancore.security.SecureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base project configuration class.
 *
 * @author vssavin on 05.12.2023.
 */
@Configuration
public class UsmanConfig {

    private static final Logger log = LoggerFactory.getLogger(UsmanConfig.class);

    private final String adminSuccessUrl;

    private final String successUrl;

    private final UsmanConfigurer usmanConfigurer;

    private final UsmanUrlsConfigurer usmanUrlsConfigurer;

    private final SecureService secureService;

    private final List<AuthorizedUrlPermission> authorizedUrlPermissions = new ArrayList<>();

    private final boolean csrfEnabled;

    private final List<PermissionPathsContainer> permissionPathsContainerList;

    private final boolean googleAuthAllowed;

    @Autowired
    public UsmanConfig(UsmanConfigurer usmanConfigurer, UsmanUrlsConfigurer usmanUrlsConfigurer,
            UsmanSecureServiceArgumentsHandler umSecureServiceArgumentsHandler,
            List<PermissionPathsContainer> permissionPathsContainerList, OAuth2Config oAuth2Config) {
        SecureService argumentsSecureService = umSecureServiceArgumentsHandler.getSecureService();
        if (argumentsSecureService == null) {
            this.secureService = usmanConfigurer.getSecureService();
        }
        else {
            this.secureService = argumentsSecureService;
        }
        log.debug("Using secure service: {}", this.secureService);
        this.usmanConfigurer = usmanConfigurer;
        this.usmanUrlsConfigurer = usmanUrlsConfigurer;
        this.permissionPathsContainerList = permissionPathsContainerList;
        this.csrfEnabled = usmanConfigurer.isCsrfEnabled();
        this.authorizedUrlPermissions.addAll(usmanConfigurer.getPermissions());
        this.adminSuccessUrl = usmanUrlsConfigurer.getAdminSuccessUrl();
        this.successUrl = usmanUrlsConfigurer.getSuccessUrl();
        this.googleAuthAllowed = !oAuth2Config.getGoogleClientId().isEmpty();
        initDefaultPermissions();
        updateAuthorizedPermissions();
    }

    private void updateAuthorizedPermissions() {
        if (!usmanConfigurer.isRegistrationAllowed()) {
            updatePermission(usmanUrlsConfigurer.getRegistrationUrl(), Permission.ADMIN_ONLY);
            updatePermission(usmanUrlsConfigurer.getPerformRegisterUrl(), Permission.ADMIN_ONLY);
            updatePermission(usmanUrlsConfigurer.getPerformRegisterUrl(), HttpMethod.POST.name(),
                    Permission.ADMIN_ONLY);
        }
    }

    public SecureService getSecureService() {
        return secureService;
    }

    public List<AuthorizedUrlPermission> getAuthorizedUrlPermissions() {
        return authorizedUrlPermissions;
    }

    public String getAdminSuccessUrl() {
        return adminSuccessUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public Pattern getPasswordPattern() {
        return usmanConfigurer.getPasswordPattern();
    }

    public String getPasswordPatternErrorMessage() {
        return usmanConfigurer.getPasswordPatternErrorMessage();
    }

    public boolean isCsrfEnabled() {
        return csrfEnabled;
    }

    public boolean isGoogleAuthAllowed() {
        return googleAuthAllowed;
    }

    private void updatePermission(String url, Permission permission) {
        int index = getPermissionIndex(url);
        String httpMethod = getPermissionHttpMethod(url);
        if (index != -1) {
            authorizedUrlPermissions.set(index, new AuthorizedUrlPermission(url, httpMethod, permission));
        }
    }

    private void updatePermission(String url, String httpMethod, Permission permission) {
        int index = getPermissionIndex(url);
        if (index != -1) {
            AuthorizedUrlPermission urlPermission = authorizedUrlPermissions.get(index);
            if (urlPermission != null && urlPermission.getHttpMethod().equals(httpMethod)) {
                authorizedUrlPermissions.set(index, new AuthorizedUrlPermission(url, httpMethod, permission));
            }
            else {
                authorizedUrlPermissions.add(new AuthorizedUrlPermission(url, httpMethod, permission));
            }
        }
    }

    private int getPermissionIndex(String url) {
        for (int i = 0; i < authorizedUrlPermissions.size(); i++) {
            AuthorizedUrlPermission authorizedUrlPermission = authorizedUrlPermissions.get(i);
            if (authorizedUrlPermission.getUrl().equals(url)) {
                return i;
            }
        }
        return -1;
    }

    private String getPermissionHttpMethod(String url) {
        for (AuthorizedUrlPermission authorizedUrlPermission : authorizedUrlPermissions) {
            if (authorizedUrlPermission.getUrl().equals(url)) {
                return authorizedUrlPermission.getHttpMethod();
            }
        }
        return AuthorizedUrlPermission.getDefaultHttpMethod();
    }

    private void initDefaultPermissions() {

        permissionPathsContainerList.forEach(container -> {
            List<AuthorizedUrlPermission> paths = container.getPermissionPaths(Permission.ANY_USER);
            authorizedUrlPermissions.addAll(paths);
            paths = container.getPermissionPaths(Permission.ADMIN_ONLY);
            authorizedUrlPermissions.addAll(paths);
            paths = container.getPermissionPaths(Permission.USER_ADMIN);
            authorizedUrlPermissions.addAll(paths);
        });
    }

}