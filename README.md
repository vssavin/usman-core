# What is it about?

This is a service layer of a simple user management system that provides CRUD (create, read, update, delete)
operations to manage users.  
This project is based on the
[spring-framework](https://github.com/spring-projects/spring-framework) and uses
[spring-security](https://github.com/spring-projects/spring-security) to provide security services. The minimum supported
version of the `spring-security` is now 5.4.3. This project supports spring5 and spring6 versions.

## Configuration

### Dependencies
To support dependencies from `github` add `jitpack` as a repository to your project. For example:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

**_Spring5 dependencies:_**

```xml
<dependency>
    <groupId>com.github.vssavin.usman-core</groupId>
    <artifactId>usman-core-spring5</artifactId>
    <version>0.0.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.github.vssavin.usman-core</groupId>
    <artifactId>usman-core</artifactId>
    <version>0.0.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.mail</groupId>
    <artifactId>javax.mail-api</artifactId>
    <version>1.6.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.sun.mail</groupId>
    <artifactId>javax.mail</artifactId>
    <version>1.5.5</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>4.4.0</version>
</dependency>
```

**_Spring6 dependencies:_**

```xml
<dependency>
    <groupId>com.github.vssavin.usman-core</groupId>
    <artifactId>usman-core-spring6</artifactId>
    <version>0.0.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.github.vssavin.usman-core</groupId>
    <artifactId>usman-core</artifactId>
    <version>0.0.2</version>
</dependency>
```

```xml
<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>2.1.1</version>
</dependency>
```

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>jakarta.mail</groupId>
    <artifactId>jakarta.mail-api</artifactId>
    <version>2.1.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.eclipse.angus</groupId>
    <artifactId>angus-activation</artifactId>
    <version>1.0.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>5.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
```

### Java code configuration

In one of your configuration classes you need to import beans from the `DefaultSecurityConfig`, `DefaultBeansConfig`
and `UsmanDataSourceConfig` classes.

```java
@Import({ DefaultSecurityConfig.class, DefaultBeansConfig.class })
```

Add the `com.github.vssavin.usmancore.*` package to @ComponentScan in your configuration class.

This project requires your configuration classes to have the following beans:

```java
@Bean
public JavaMailSender emailSender() {
    return new JavaMailSenderImpl();
}

@Bean
public UsmanConfigurer usmanConfigurer() {
    UsmanConfigurer usmanConfigurer = new UsmanConfigurer();
    usmanConfigurer.permission(new AuthorizedUrlPermission("/index.html",Permission.ANY_USER))
        .permission(new AuthorizedUrlPermission("/index",Permission.ANY_USER));
    return usmanConfigurer.configure();
}

@Bean
public UsmanUrlsConfigurer usmanUrlsConfigurer() {
    UsmanUrlsConfigurer usmanUrlsConfigurer = new UsmanUrlsConfigurer();
    usmanUrlsConfigurer.successUrl("/index.html");
    return usmanUrlsConfigurer.configure();
}
```

This is a simple example of the configuration class you need:
```java
@Configuration
@ComponentScan({ "com.github.vssavin.usmancore" })
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.github.vssavin.usmancore")
@EnableWebSecurity
@Import({ DefaultSecurityConfig.class, DefaultBeansConfig.class, UsmanDataSourceConfig.class })
public class ApplicationConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public JavaMailSender emailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public UsmanConfigurer usmanConfigurer() {
        UsmanConfigurer usmanConfigurer = new UsmanConfigurer();
        usmanConfigurer.permission(new AuthorizedUrlPermission("/index.html", Permission.ANY_USER))
            .permission(new AuthorizedUrlPermission("/index", Permission.ANY_USER));
        return usmanConfigurer.configure();
    }

    @Bean
    public UsmanUrlsConfigurer usmanUrlsConfigurer() {
        UsmanUrlsConfigurer usmanUrlsConfigurer = new UsmanUrlsConfigurer();
        usmanUrlsConfigurer.successUrl("/index.html");
        return usmanUrlsConfigurer.configure();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("routingDatasource") DataSource routingDatasource, DatabaseConfig databaseConfig) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        try {
            em.setDataSource(routingDatasource);
            em.setPackagesToScan("com.github.vssavin.usmancore");

            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            String hibernateDialect = databaseConfig.getDialect();

            Properties additionalProperties = new Properties();
            additionalProperties.put("hibernate.dialect", hibernateDialect);
            em.setJpaProperties(additionalProperties);
        }
        catch (Exception e) {
            log.error("Creating LocalContainerEntityManagerFactoryBean error!", e);
        }

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * Datasource bean of your project. The bean name must be `appDatasource`
     */
    @Bean
    @Primary
    public DataSource appDatasource() {
        
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("init.sql")
                .build();
    }

}
```

