package com.github.vssavin.usmancore.spring6.security.rememberme;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Main repository of remember-me.
 *
 * @author vssavin on 11.12.2023.
 */
@Repository
public interface UserRememberMeTokenRepository extends CrudRepository<UserRememberMeToken, Long> {

    @UsmanRouteDatasource
    List<UserRememberMeToken> findByUserId(Long userId);

    @UsmanRouteDatasource
    UserRememberMeToken save(UserRememberMeToken entity);

}
