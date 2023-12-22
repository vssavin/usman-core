package com.github.vssavin.usmancore.config;

import com.github.vssavin.usmancore.security.SecureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An {@link com.github.vssavin.usmancore.config.AbstractApplicationArgumentsHandler}
 * implementation that processes application arguments to initialize a secure service.
 *
 * @author vssavin on 29.11.2023.
 */
@Configuration
public class UsmanSecureServiceArgumentsHandler extends AbstractApplicationArgumentsHandler {

    private static final String SECURE_SERVICE_PROP_NAME = "usman.secureService";

    private static final Logger log = LoggerFactory.getLogger(UsmanSecureServiceArgumentsHandler.class);

    private final ApplicationContext context;

    private final SecureService defaultSecureService;

    private final UsmanConfigurer usmanConfigurer;

    private final List<ArgumentsProcessedNotifier> notifiers;

    private SecureService secureService;

    @Value("${" + SECURE_SERVICE_PROP_NAME + ":#{null}}")
    private String secureServiceName;

    @Autowired
    UsmanSecureServiceArgumentsHandler(ApplicationContext applicationContext, UsmanConfigurer usmanConfigurer,
            SecureService secureService, List<ArgumentsProcessedNotifier> notifiers) {
        super(log, applicationContext);
        this.context = applicationContext;
        this.usmanConfigurer = usmanConfigurer;
        this.defaultSecureService = secureService;
        this.notifiers = notifiers;
    }

    @Override
    public void processArgs() {
        String serviceName = getServiceName();
        if (!serviceName.isEmpty()) {
            this.secureServiceName = getServiceName();
        }

        if (this.secureServiceName == null || this.secureServiceName.isEmpty()) {
            if (usmanConfigurer.getSecureService() == null) {
                log.warn("Secure service not specified! Using default secure service...");
                this.secureService = defaultSecureService;
                usmanConfigurer.changeSecureService(this.secureService);
            }
        }
        else {
            initSecureService(this.secureServiceName);
            usmanConfigurer.changeSecureService(this.secureService);
        }

        notifiers.forEach(notifier -> notifier.notifyArgumentsProcessed(this.getClass()));
    }

    SecureService getSecureService() {
        return secureService;
    }

    static String getSecureServicePropName() {
        return SECURE_SERVICE_PROP_NAME;
    }

    private String getServiceName() {
        String serviceName = "";
        String[] args = getApplicationArguments();
        if (args.length > 0) {
            String argsString = Arrays.toString(args);
            log.debug("Application started with arguments: {}", argsString);
            Map<String, String> mappedArgs = getMappedArgs(args);
            serviceName = mappedArgs.get(SECURE_SERVICE_PROP_NAME);
        }

        if (serviceName == null) {
            serviceName = System.getProperty(SECURE_SERVICE_PROP_NAME);
        }

        if (serviceName == null) {
            serviceName = System.getenv(SECURE_SERVICE_PROP_NAME);
        }

        if (serviceName == null) {
            serviceName = "";
        }

        return serviceName;
    }

    private void initSecureService(String secureServiceName) {
        if (context == null || defaultSecureService == null) {
            throw new IllegalStateException("Not initialized application context or default secure service!");
        }

        if (secureServiceName != null && !secureServiceName.isEmpty()) {
            secureService = getSecureServiceByName(secureServiceName);
        }
        else {
            log.warn("Secure service not specified! Using default secure service...");
            secureService = defaultSecureService;
        }
    }

    private SecureService getSecureServiceByName(String serviceName) {
        SecureService service = null;
        boolean beanFound = true;
        try {
            service = (SecureService) context.getBean(serviceName + "SecureService");
        }
        catch (NoSuchBeanDefinitionException ignore) {
            try {
                service = (SecureService) context.getBean(serviceName.toUpperCase() + "SecureService");
            }
            catch (NoSuchBeanDefinitionException e) {
                beanFound = false;
            }
        }
        if (!beanFound) {
            throw new IllegalArgumentException(String.format("Service with name %s not found!", serviceName));
        }
        return service;
    }

}
