package com.github.vssavin.usmancore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * Ensures that processing of application arguments for each component begins after it is
 * created.
 *
 * @author vssavin on 20.12.2023.
 */
@Configuration
public class ArgumentsHandlerStarter implements ApplicationListener<ContextRefreshedEvent> {

    private final List<AbstractApplicationArgumentsHandler> argumentsHandlerList;

    @Autowired
    public ArgumentsHandlerStarter(List<AbstractApplicationArgumentsHandler> argumentsHandlerList) {
        this.argumentsHandlerList = argumentsHandlerList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        argumentsHandlerList.forEach(AbstractApplicationArgumentsHandler::processArgs);
    }

}
