package com.github.vssavin.usmancore.spring6;

import com.github.vssavin.usmancore.spring6.config.ApplicationConfig;
import com.github.vssavin.usmancore.spring6.config.DatabaseConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author vssavin on 13.12.2023
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("usman-test")
@ContextConfiguration(classes = { ApplicationConfig.class, DatabaseConfig.class })
@WebAppConfiguration
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
public class StartingSpringBootTest {

    @Test
    public void contextLoads() {

    }

}
