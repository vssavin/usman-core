package com.github.vssavin.usmancore.user;

import com.github.vssavin.usmancore.event.UsmanEvent;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

public interface UsmanUser extends UserDetails {

    Long getId();

    String getLogin();

    String getName();

    String getEmail();

    String getAuthority();

    Date getExpirationDate();

    String getVerificationId();

    List<UsmanEvent> getEvents();

    void setId(Long id);

    void setLogin(String login);

    void setName(String name);

    void setPassword(String password);

    void setEmail(String email);

    void setAuthority(String authority);

    void setExpirationDate(Date expirationDate);

    void setVerificationId(String verificationId);

    void setAccountLocked(boolean accountNonLocked);

    void setCredentialsExpired(boolean credentialsNonExpired);

    void setEnabled(boolean enabled);

    void setEvents(List<UsmanEvent> usmanEvents);

    void addEvent(UsmanEvent usmanEvent);

}
