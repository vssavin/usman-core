package com.github.vssavin.usmancore.spring5;

import com.github.vssavin.usmancore.spring5.config.ApplicationConfig;
import org.junit.Ignore;
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
@ContextConfiguration(classes = { ApplicationConfig.class })
@WebAppConfiguration
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
public class StartingSpringBootTest {

    @Test
    @Ignore("Can't load context using java 8 (because nashorn engine used)")
    public void contextLoads() {

    }

}
