package com.github.vssavin.usmancore.spring5.security.csrf;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Main repository of csrf.
 *
 * @author vssavin on 11.12.2023.
 */
public interface UserCsrfTokenRepository extends CrudRepository<UserCsrfToken, Long> {

    @UsmanRouteDatasource
    List<UserCsrfToken> findByUserId(Long userId);

    @UsmanRouteDatasource
    @Transactional
    void deleteByToken(String token);

    @UsmanRouteDatasource
    @Transactional
    void deleteByUserId(Long userId);

}
