package com.github.vssavin.usmancore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

/**
 * Contains default beans for user management.
 *
 * @author vssavin on 12.12.2023.
 */
public class DefaultBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SqlScriptExecutor sqlScriptExecutor(DataSource usmanDataSource) {
        SqlScriptExecutor sqlScriptExecutor = new SqlScriptExecutor(usmanDataSource);
        List<String> scriptsList = Collections.singletonList("init.sql");
        sqlScriptExecutor.executeSqlScriptsFromResource(scriptsList, "");
        return sqlScriptExecutor;
    }

}
