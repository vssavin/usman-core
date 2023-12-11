package com.github.vssavin.usmancore.spring6.user;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Main repository of user management.
 *
 * @author vssavin on 06.12.2023.
 */
@Repository
public interface UserRepository
        extends PagingAndSortingRepository<User, Long>, QuerydslPredicateExecutor<User>, CrudRepository<User, Long> {

    List<User> findByLogin(String login);

    List<User> findUserByName(String name);

    List<User> findByEmail(String email);

    @Transactional
    void deleteByLogin(String login);

}
