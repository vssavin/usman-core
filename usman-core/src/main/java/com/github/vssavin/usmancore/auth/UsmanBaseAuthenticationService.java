package com.github.vssavin.usmancore.auth;

import com.github.vssavin.usmancore.config.UsmanConfigurer;
import com.github.vssavin.usmancore.security.SecureService;
import com.github.vssavin.usmancore.security.auth.UsmanUsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main implementation of {@link com.github.vssavin.usmancore.auth.UsmanBaseAuthenticator}
 * interface.
 *
 * @author vssavin on 11.12.2023.
 */
public class UsmanBaseAuthenticationService implements UsmanBaseAuthenticator {

    private static final Class<? extends Authentication> authenticationClass = UsmanUsernamePasswordAuthenticationToken.class;

    private static final ConcurrentHashMap<String, Integer> blackList = new ConcurrentHashMap<>(50);

    private static final ConcurrentHashMap<String, Long> banExpirationTime = new ConcurrentHashMap<>(50);

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final SecureService secureService;

    protected final int maxFailureCount;

    protected final int blockTimeMinutes;

    public UsmanBaseAuthenticationService(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
            SecureService secureService, UsmanConfigurer usmanConfigurer) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.secureService = secureService;
        this.maxFailureCount = usmanConfigurer.getMaxAuthFailureCount();
        this.blockTimeMinutes = usmanConfigurer.getAuthFailureBlockTimeMinutes();
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        Object userName = authentication.getPrincipal();
        if (credentials != null) {
            UserDetails user = userDetailsService.loadUserByUsername(userName.toString());
            if (user != null) {
                checkUserDetails(user);

                String addr = getRemoteAddress(authentication);
                String password = secureService.decrypt(credentials.toString(), secureService.getPrivateKey(addr));

                if (passwordEncoder.matches(password, user.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
                    return new UsmanUsernamePasswordAuthenticationToken(authentication.getPrincipal(), password,
                            authorities);
                }
                else {
                    throw new BadCredentialsException("Authentication failed");
                }

            }
            else {
                return authentication;
            }

        }
        else {
            return authentication;
        }
    }

    @Override
    public boolean isAuthenticationAllowed(String authId) {
        int failureCounts = getFailureCount(authId);
        if (failureCounts >= maxFailureCount) {
            long expireTime = getBanExpirationTime(authId);
            if (expireTime > 0 && System.currentTimeMillis() < expireTime) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class<? extends Authentication> authenticationClass() {
        return authenticationClass;
    }

    private void checkUserDetails(UserDetails userDetails) {
        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("Account is expired!");
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("Account is locked!");
        }
        if (!userDetails.isEnabled()) {
            throw new DisabledException("Account is disabled!");
        }
    }

    private String getRemoteAddress(Authentication authentication) {
        Object details = authentication.getDetails();

        if (details instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) details).getRemoteAddress();
        }

        return "";
    }

    protected int getFailureCount(String userIp) {
        return blackList.getOrDefault(userIp, 1);
    }

    protected void incrementFailureCount(String userIp) {
        int failureCounts = getFailureCount(userIp) + 1;
        blackList.put(userIp, failureCounts);
    }

    protected void resetFailureCount(String userIp) {
        blackList.put(userIp, 1);
        banExpirationTime.remove(userIp);
    }

    protected long getBanExpirationTime(String userIp) {
        return banExpirationTime.getOrDefault(userIp, 0L);
    }

    protected void blockIp(String ip) {
        banExpirationTime.put(ip, Calendar.getInstance().getTimeInMillis() + ((long) blockTimeMinutes * 60 * 1000));
    }

}
