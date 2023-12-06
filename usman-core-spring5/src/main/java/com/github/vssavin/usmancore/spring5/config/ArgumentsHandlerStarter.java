package com.github.vssavin.usmancore.spring5.config;

import com.github.vssavin.usmancore.config.AbstractApplicationArgumentsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Ensures that processing of application arguments for each component begins after it is
 * created.
 *
 * @author vssavin on 29.11.2023
 */
@Configuration
public class ArgumentsHandlerStarter {

    private final List<AbstractApplicationArgumentsHandler> argumentsHandlerList;

    @Autowired
    public ArgumentsHandlerStarter(List<AbstractApplicationArgumentsHandler> argumentsHandlerList) {
        this.argumentsHandlerList = argumentsHandlerList;
    }

    @PostConstruct
    private void processArgs() {
        argumentsHandlerList.forEach(AbstractApplicationArgumentsHandler::processArgs);
    }

}
