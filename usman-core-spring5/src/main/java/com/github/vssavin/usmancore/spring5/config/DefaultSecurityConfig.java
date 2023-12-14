package com.github.vssavin.usmancore.spring5.config;

import com.github.vssavin.usmancore.config.*;
import com.github.vssavin.usmancore.spring5.security.auth.UsmanBlackListFilter;
import com.github.vssavin.usmancore.spring5.security.csrf.UmCsrfTokenRepository;
import com.github.vssavin.usmancore.spring5.security.csrf.UserCsrfTokenRepository;
import com.github.vssavin.usmancore.spring5.security.rememberme.Authenticator;
import com.github.vssavin.usmancore.spring5.security.rememberme.RefreshOnLoginDatabaseTokenBasedRememberMeService;
import com.github.vssavin.usmancore.spring5.security.rememberme.UserRememberMeTokenRepository;
import com.github.vssavin.usmancore.spring5.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder,
            AuthenticationProvider usmanAuthenticationProvider) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userService)
            .passwordEncoder(passwordEncoder)
            .and()
            .authenticationProvider(usmanAuthenticationProvider)
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, UsmanConfig usmanConfig,
            UsmanUrlsConfigurer urlsConfigurer, UsmanBlackListFilter blackListFilter) throws Exception {

        httpSecurity.addFilterBefore(blackListFilter, BasicAuthenticationFilter.class);

        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

        List<AuthorizedUrlPermission> urlPermissions = usmanConfig.getAuthorizedUrlPermissions();

        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = registerUrls(
                httpSecurity, urlPermissions);

        HttpSecurity security = registry.and();

        String secretKey = UUID.randomUUID().toString();

        AbstractRememberMeServices rememberMeServices = new RefreshOnLoginDatabaseTokenBasedRememberMeService(secretKey,
                userService, rememberMeTokenRepository);
        rememberMeServices.setAlwaysRemember(true);

        Authenticator authenticator = (Authenticator) rememberMeServices;

        if (!usmanConfig.isCsrfEnabled()) {
            security.csrf().disable();
        }
        else {
            UmCsrfTokenRepository umCsrfTokenRepository = new UmCsrfTokenRepository(authenticator, csrfTokenRepository,
                    rememberMeTokenRepository);
            umCsrfTokenRepository.setUseCache(true);
            security.csrf().csrfTokenRepository(umCsrfTokenRepository).and();
        }

        security.formLogin()
            .failureHandler(authFailureHandler)
            .successHandler(authSuccessHandler)
            .loginPage(urlsConfigurer.getLoginUrl())
            .loginProcessingUrl(urlsConfigurer.getLoginProcessingUrl())
            .usernameParameter("username")
            .passwordParameter("password")
            .and()
            .logout()
            .permitAll()
            .logoutUrl(urlsConfigurer.getLogoutUrl())
            .logoutSuccessHandler(logoutSuccessHandler)
            .invalidateHttpSession(true);

        security.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        security.rememberMe(customizer -> customizer.userDetailsService(userService)
            .rememberMeServices(rememberMeServices)
            .key(secretKey)
            .alwaysRemember(true));

        if (!Objects.equals(oAuth2Config.getGoogleClientId(), "")) {
            registry.and()
                .oauth2Login()
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                .loginPage(urlsConfigurer.getLoginUrl())
                .userInfoEndpoint()
                .userService(customOAuth2UserService);
        }

        return httpSecurity.build();
    }

    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registerUrls(
            HttpSecurity http, List<AuthorizedUrlPermission> urlPermissions) throws Exception {

        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = http
            .authorizeHttpRequests();

        List<AuthorizedUrlPermission> permissions = new ArrayList<>(urlPermissions);
        permissions.sort(Comparator.comparingInt(o -> o.getRoles().length));

        for (AuthorizedUrlPermission urlPermission : permissions) {
            String[] roles = urlPermission.getRoles();
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = registry
                .requestMatchers(new AntPathRequestMatcher(urlPermission.getUrl(), urlPermission.getHttpMethod()));

            if (roles != null && roles.length == 0) {
                registry = authorizedUrl.permitAll();
            }
            else if (roles != null) {
                registry = authorizedUrl.hasAnyRole(urlPermission.getRoles());
            }
        }

        return registry;
    }

}
