package com.github.vssavin.usmancore.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Configuration class to connect to email service.
 *
 * @author vssavin on 08.12.2023.
 */
@Component
@PropertySource(value = "file:./mail.properties", ignoreResourceNotFound = true)
public class EmailConfig {

    public static final String NAME_PREFIX = "mail";

    @Value("${" + NAME_PREFIX + ".host:}")
    private String host;

    @Value("${" + NAME_PREFIX + ".port:587}")
    private int port;

    @Value("${" + NAME_PREFIX + ".userName:}")
    private String userName;

    @Value("${" + NAME_PREFIX + ".password:}")
    private String password;

    @Value("${" + NAME_PREFIX + ".protocol:smtp}")
    private String protocol;

    @Value("${" + NAME_PREFIX + ".smtpPort:25}")
    private int smtpPort;

    @Value("${" + NAME_PREFIX + ".smtpAuth:true}")
    private boolean smtpAuth;

    @Value("${" + NAME_PREFIX + ".tlsEnabled:true}")
    private boolean tlsEnabled;

    @Value("${" + NAME_PREFIX + ".tlsRequired:true}")
    private boolean tlsRequired;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public boolean isTlsRequired() {
        return tlsRequired;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public void setTlsRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
    }

}
