package com.github.vssavin.usmancore.config;

import com.github.vssavin.usmancore.security.SecureService;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configures user management params.
 *
 * @author vssavin on 05.12.2023.
 */
public class UsmanConfigurer {

    private String loginPageTitle = "";

    private String applicationUrl = "http://127.0.0.1:8085";

    private SecureService secureService;

    private Pattern passwordPattern;

    private UsmanAuthPasswordConfig passwordConfig;

    private String passwordPatternErrorMessage = "Wrong password!";

    private List<AuthorizedUrlPermission> permissions = new ArrayList<>();

    private final Map<String, String[]> resourceHandlers = new HashMap<>();

    private boolean csrfEnabled = true;

    private boolean configured = false;

    private boolean registrationAllowed = true;

    private int maxAuthFailureCount = 3;

    private int authFailureBlockTimeMinutes = 60;

    public UsmanConfigurer loginPageTitle(String loginPageTitle) {
        checkAccess();
        this.loginPageTitle = loginPageTitle;
        return this;
    }

    public UsmanConfigurer applicationUrl(String applicationUrl) {
        checkAccess();
        this.applicationUrl = applicationUrl;
        return this;
    }

    public UsmanConfigurer secureService(SecureService secureService) {
        checkAccess();
        this.secureService = secureService;
        return this;
    }

    public UsmanConfigurer passwordPatternErrorMessage(String passwordPatternErrorMessage) {
        checkAccess();
        this.passwordPatternErrorMessage = passwordPatternErrorMessage;
        return this;
    }

    public UsmanConfigurer permissions(List<AuthorizedUrlPermission> permissions) {
        checkAccess();
        this.permissions = permissions;
        return this;
    }

    public UsmanConfigurer permission(AuthorizedUrlPermission permission) {
        checkAccess();
        this.permissions.add(permission);
        return this;
    }

    public UsmanConfigurer csrf(boolean enabled) {
        checkAccess();
        this.csrfEnabled = enabled;
        return this;
    }

    public UsmanConfigurer registrationAllowed(boolean registrationAllowed) {
        checkAccess();
        this.registrationAllowed = registrationAllowed;
        return this;
    }

    public UsmanConfigurer maxAuthFailureCount(int maxAuthFailureCount) {
        checkAccess();
        this.maxAuthFailureCount = maxAuthFailureCount;
        return this;
    }

    public UsmanConfigurer authFailureBlockTimeMinutes(int authFailureBlockTimeMinutes) {
        checkAccess();
        this.authFailureBlockTimeMinutes = authFailureBlockTimeMinutes;
        return this;
    }

    public UsmanConfigurer resourceHandlers(Map<String, String[]> resourceHandlers) {
        checkAccess();
        resourceHandlers.forEach((handler, locations) -> {
            String[] existsLocations = this.resourceHandlers.get(handler);
            if (existsLocations != null) {
                String[] newLocations = Arrays.copyOf(existsLocations, existsLocations.length + locations.length);
                System.arraycopy(locations, 0, newLocations, existsLocations.length, locations.length);
                this.resourceHandlers.put(handler, newLocations);
            }
            else {
                this.resourceHandlers.put(handler, locations);
            }
        });
        return this;
    }

    public UsmanConfigurer configure() {
        this.configured = true;
        return this;
    }

    public String getLoginPageTitle() {
        return loginPageTitle;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public SecureService getSecureService() {
        return secureService;
    }

    public Pattern getPasswordPattern() {
        if (passwordPattern == null) {
            if (passwordConfig == null) {
                passwordConfig = new UsmanAuthPasswordConfig();
            }
            passwordPattern = initPasswordPattern(passwordConfig);
        }
        return passwordPattern;
    }

    public String getPasswordPatternErrorMessage() {
        return passwordPatternErrorMessage;
    }

    public List<AuthorizedUrlPermission> getPermissions() {
        return permissions;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public int getMaxAuthFailureCount() {
        return maxAuthFailureCount;
    }

    public int getAuthFailureBlockTimeMinutes() {
        return authFailureBlockTimeMinutes;
    }

    public UsmanAuthPasswordConfig passwordConfig() {
        this.passwordConfig = new UsmanAuthPasswordConfig();
        return this.passwordConfig;
    }

    public Map<String, String[]> getResourceHandlers() {
        return resourceHandlers;
    }

    public boolean isCsrfEnabled() {
        return csrfEnabled;
    }

    @Override
    public String toString() {
        return "UsmanConfigurer{" + "loginPageTitle='" + loginPageTitle + '\'' + ", applicationUrl='" + applicationUrl
                + '\'' + ", secureService=" + secureService + ", passwordPattern=" + passwordPattern
                + ", passwordConfig=" + passwordConfig + ", passwordPatternErrorMessage='" + passwordPatternErrorMessage
                + '\'' + ", permissions=" + permissions + ", resourceHandlers=" + resourceHandlers + ", csrfEnabled="
                + csrfEnabled + ", configured=" + configured + ", registrationAllowed=" + registrationAllowed
                + ", maxAuthFailureCount=" + maxAuthFailureCount + ", authFailureBlockTimeMinutes="
                + authFailureBlockTimeMinutes + '}';
    }

    private void checkAccess() {
        if (configured) {
            throw new IllegalStateException("UsmanConfigurer is already configured!");
        }
    }

    void changeSecureService(SecureService secureService) {
        this.secureService = secureService;
    }

    private Pattern initPasswordPattern(UsmanAuthPasswordConfig passwordConfig) {
        StringBuilder stringPatternBuilder = new StringBuilder("^");
        if (passwordConfig.isAtLeastOneDigit()) {
            stringPatternBuilder.append("(?=.*[0-9])");
        }

        if (passwordConfig.isAtLeastOneLowerCaseLatin()) {
            stringPatternBuilder.append("(?=.*[a-z])");
        }

        if (passwordConfig.isAtLeastOneUpperCaseLatin()) {
            stringPatternBuilder.append("(?=.*[A-Z])");
        }

        if (passwordConfig.isAtLeastOneSpecialCharacter()) {
            stringPatternBuilder.append("(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])");
        }

        stringPatternBuilder.append(".").append("{").append(passwordConfig.getMinLength()).append(",");

        if (passwordConfig.getMaxLength() != 0) {
            stringPatternBuilder.append(passwordConfig.getMaxLength());
        }

        stringPatternBuilder.append("}$");

        return Pattern.compile(stringPatternBuilder.toString());
    }

}
