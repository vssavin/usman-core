package com.github.vssavin.usmancore.spring6.config;

import com.github.vssavin.usmancore.config.*;
import com.github.vssavin.usmancore.spring6.security.auth.UsmanBlackListFilter;
import com.github.vssavin.usmancore.spring6.security.csrf.UmCsrfTokenRepository;
import com.github.vssavin.usmancore.spring6.security.csrf.UserCsrfTokenRepository;
import com.github.vssavin.usmancore.spring6.security.rememberme.Authenticator;
import com.github.vssavin.usmancore.spring6.security.rememberme.RefreshOnLoginDatabaseTokenBasedRememberMeService;
import com.github.vssavin.usmancore.spring6.security.rememberme.UserRememberMeTokenRepository;
import com.github.vssavin.usmancore.spring6.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.*;

/**
 * Provides default user management configuration for spring-security.
 *
 * @author vssavin on 11.12.2023.
 */
public class DefaultSecurityConfig {

    private final UserService userService;

    private final AuthenticationSuccessHandler authSuccessHandler;

    private final AuthenticationFailureHandler authFailureHandler;

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    private final LogoutSuccessHandler logoutSuccessHandler;

    private final OAuth2Config oAuth2Config;

    private final UserRememberMeTokenRepository rememberMeTokenRepository;

    private final UserCsrfTokenRepository csrfTokenRepository;

    @Autowired
    public DefaultSecurityConfig(UserService userService, DefaultAuthConfig defaultAuthConfig,
            OAuth2Config oAuth2Config, UserRememberMeTokenRepository rememberMeTokenRepository,
            UserCsrfTokenRepository csrfTokenRepository) {
        this.userService = userService;
        this.authSuccessHandler = defaultAuthConfig.getAuthSuccessHandler();
        this.authFailureHandler = defaultAuthConfig.getAuthFailureHandler();
        this.customOAuth2UserService = defaultAuthConfig.getOAuth2UserService();
        this.logoutSuccessHandler = defaultAuthConfig.getLogoutSuccessHandler();
        this.oAuth2Config = oAuth2Config;
        this.rememberMeTokenRepository = rememberMeTokenRepository;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder,
            AuthenticationProvider usmanAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        builder.authenticationProvider(usmanAuthenticationProvider);
        return builder.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, UsmanConfigurer usmanConfigurer,
            UsmanUrlsConfigurer urlsConfigurer, UsmanBlackListFilter blackListFilter) throws Exception {

        security.addFilterBefore(blackListFilter, BasicAuthenticationFilter.class);

        List<AuthorizedUrlPermission> urlPermissions = usmanConfigurer.getPermissions();
        registerUrls(security, urlPermissions);

        String secretKey = UUID.randomUUID().toString();

        AbstractRememberMeServices rememberMeServices = new RefreshOnLoginDatabaseTokenBasedRememberMeService(secretKey,
                userService, rememberMeTokenRepository);
        rememberMeServices.setAlwaysRemember(true);
        rememberMeServices.setTokenValiditySeconds(usmanConfigurer.getRememberMeTokenValiditySeconds());

        Authenticator authenticator = (Authenticator) rememberMeServices;

        if (!usmanConfigurer.isCsrfEnabled()) {
            security.csrf(AbstractHttpConfigurer::disable);
        }
        else {
            UmCsrfTokenRepository umCsrfTokenRepository = new UmCsrfTokenRepository(authenticator, csrfTokenRepository,
                    rememberMeTokenRepository);
            umCsrfTokenRepository.setTokenValiditySeconds(usmanConfigurer.getCsrfTokenValiditySeconds());
            umCsrfTokenRepository.setUseCache(true);
            security.csrf(configurer -> configurer.csrfTokenRepository(umCsrfTokenRepository));
        }

        security.formLogin(configurer -> configurer.failureHandler(authFailureHandler)
            .successHandler(authSuccessHandler)
            .loginPage(urlsConfigurer.getLoginUrl())
            .loginProcessingUrl(urlsConfigurer.getLoginProcessingUrl())
            .usernameParameter("username")
            .passwordParameter("password"));

        security.logout(configurer -> configurer.permitAll()
            .logoutUrl(urlsConfigurer.getLogoutUrl())
            .logoutRequestMatcher(createLogoutRequestMatcher(urlsConfigurer.getLogoutUrl()))
            .logoutSuccessHandler(logoutSuccessHandler)
            .invalidateHttpSession(true));

        security.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        security.rememberMe(customizer -> customizer.userDetailsService(userService)
            .rememberMeServices(rememberMeServices)
            .key(secretKey)
            .alwaysRemember(true));

        if (!Objects.equals(oAuth2Config.getGoogleClientId(), "")) {
            security.oauth2Login(configurer -> configurer.successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                .loginPage(urlsConfigurer.getLoginUrl())
                .userInfoEndpoint(userInfoConfigurer -> userInfoConfigurer.userService(customOAuth2UserService)));
        }

        return security.build();
    }

    private RequestMatcher createLogoutRequestMatcher(String url) {
        return new AntPathRequestMatcher(url, "POST");
    }

    private void registerUrls(HttpSecurity http, List<AuthorizedUrlPermission> urlPermissions) throws Exception {

        List<AuthorizedUrlPermission> permissions = new ArrayList<>(urlPermissions);
        permissions.sort(Comparator.comparingInt(o -> o.getRoles().length));

        http.authorizeHttpRequests(configurer -> {
            for (AuthorizedUrlPermission urlPermission : permissions) {
                String[] roles = urlPermission.getRoles();
                AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = configurer
                    .requestMatchers(new AntPathRequestMatcher(urlPermission.getUrl(), urlPermission.getHttpMethod()));

                if (roles != null && roles.length == 0) {
                    configurer = authorizedUrl.permitAll();
                }
                else if (roles != null) {
                    configurer = authorizedUrl.hasAnyRole(urlPermission.getRoles());
                }
            }
        });
    }

}
