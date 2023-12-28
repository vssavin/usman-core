package com.github.vssavin.usmancore.aspect;

import com.github.vssavin.usmancore.config.DataSourceSwitcher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Ensures that the datasource is switched before and after executing methods marked with
 * the {@link com.github.vssavin.usmancore.aspect.UsmanRouteDatasource} annotation.
 *
 * @author vssavin on 05.12.2023.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class UsmanRoutingDatasourceAspect {

    private static final Logger log = LoggerFactory.getLogger(UsmanRoutingDatasourceAspect.class);

    private final DataSourceSwitcher dataSourceSwitcher;

    @Autowired
    UsmanRoutingDatasourceAspect(DataSourceSwitcher dataSourceSwitcher) {
        this.dataSourceSwitcher = dataSourceSwitcher;
    }

    @Around("@annotation(UsmanRouteDatasource)")
    public Object routeDatasource(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Switching to usman database...");
        Object result;
        dataSourceSwitcher.switchToUmDataSource();
        try {
            result = joinPoint.proceed();
        }
        finally {
            dataSourceSwitcher.switchToPreviousDataSource();
            log.debug("Switching back from usman database...");
        }

        return result;
    }

}
