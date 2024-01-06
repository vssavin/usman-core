package com.github.vssavin.usmancore.config;

/**
 * Configures user management urls.
 *
 * @author vssavin on 06.12.2023
 */
public class UsmanUrlsConfigurer {

    private String loginUrl = "/login";

    private String loginProcessingUrl = "/perform-login";

    private String logoutUrl = "/logout";

    private String performLogoutUrl = "/perform-logout";

    private String successUrl = "/index.html";

    private String adminSuccessUrl = "/usman/admin";

    private String registrationUrl = "/usman/users/registration";

    private String performRegisterUrl = "/usman/users/perform-register";

    private String adminUrl = "/usman/admin";

    private boolean configured = false;

    public UsmanUrlsConfigurer loginUrl(String loginUrl) {
        checkAccess();
        this.loginUrl = loginUrl;
        return this;
    }

    public UsmanUrlsConfigurer loginProcessingUrl(String loginProcessingUrl) {
        checkAccess();
        this.loginProcessingUrl = loginProcessingUrl;
        return this;
    }

    public UsmanUrlsConfigurer logoutUrl(String logoutUrl) {
        checkAccess();
        this.logoutUrl = logoutUrl;
        return this;
    }

    public UsmanUrlsConfigurer performLogoutUrl(String performLogoutUrl) {
        checkAccess();
        this.performLogoutUrl = performLogoutUrl;
        return this;
    }

    public UsmanUrlsConfigurer successUrl(String successUrl) {
        checkAccess();
        this.successUrl = successUrl;
        return this;
    }

    public UsmanUrlsConfigurer adminSuccessUrl(String adminSuccessUrl) {
        checkAccess();
        this.adminSuccessUrl = adminSuccessUrl;
        return this;
    }

    public UsmanUrlsConfigurer registrationUrl(String registrationUrl) {
        checkAccess();
        this.registrationUrl = registrationUrl;
        return this;
    }

    public UsmanUrlsConfigurer performRegisterUrl(String performRegisterUrl) {
        checkAccess();
        this.performRegisterUrl = performRegisterUrl;
        return this;
    }

    public UsmanUrlsConfigurer adminUrl(String adminUrl) {
        checkAccess();
        this.adminUrl = adminUrl;
        return this;
    }

    public UsmanUrlsConfigurer configure() {
        this.configured = true;
        return this;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getPerformLogoutUrl() {
        return performLogoutUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getAdminSuccessUrl() {
        return adminSuccessUrl;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public String getPerformRegisterUrl() {
        return performRegisterUrl;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public boolean isConfigured() {
        return configured;
    }

    private void checkAccess() {
        if (configured) {
            throw new IllegalStateException("UsmanConfigurer is already configured!");
        }
    }

    @Override
    public String toString() {
        return "UsmanUrlsConfigurer{" + "loginUrl='" + loginUrl + '\'' + ", loginProcessingUrl='" + loginProcessingUrl
                + '\'' + ", logoutUrl='" + logoutUrl + '\'' + ", performLogoutUrl='" + performLogoutUrl + '\''
                + ", successUrl='" + successUrl + '\'' + ", adminSuccessUrl='" + adminSuccessUrl + '\''
                + ", registrationUrl='" + registrationUrl + '\'' + ", performRegisterUrl='" + performRegisterUrl + '\''
                + ", adminUrl='" + adminUrl + '\'' + ", configured=" + configured + '}';
    }

}
