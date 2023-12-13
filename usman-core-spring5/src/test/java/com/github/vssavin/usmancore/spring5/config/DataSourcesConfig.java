package com.github.vssavin.usmancore.spring5.config;

import com.github.vssavin.usmancore.config.UsmanDataSourceConfig;
import org.springframework.context.annotation.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

/**
 * @author vssavin on 13.12.2023.
 */
@Configuration
@Import(UsmanDataSourceConfig.class)
public class DataSourcesConfig {

    private DataSource appDataSource;

    @Bean
    @Primary
    public DataSource appDatasource() {
        if (appDataSource == null) {
            appDataSource = new EmbeddedDatabaseBuilder(
                    new DefaultResourceLoader(UsmanDataSourceConfig.class.getClassLoader()))
                .generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("com/github/vssavin/usmancore/config/init.sql")
                .build();
        }
        return appDataSource;
    }

    @Bean("usmanDataSource")
    @Profile("usman-test")
    public DataSource usmanDataSourceTest() {
        return appDatasource();
    }

}
