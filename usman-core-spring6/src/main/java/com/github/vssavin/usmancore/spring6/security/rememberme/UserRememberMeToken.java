package com.github.vssavin.usmancore.spring6.security.rememberme;

import com.github.vssavin.usmancore.spring6.user.User;
import jakarta.persistence.*;

/**
 * Base remember-me entity.
 *
 * @author vssavin on 11.12.2023.
 */
@Entity
@Table(name = "rememberme_tokens")
public class UserRememberMeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public UserRememberMeToken(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public UserRememberMeToken() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserRememberMeToken that = (UserRememberMeToken) o;

        if (!userId.equals(that.userId)) {
            return false;
        }
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserRememberMeToken{" + "id=" + id + ", userId=" + userId + ", token='" + token + '\'' + '}';
    }

}
