package com.github.vssavin.usmancore.aspect;

import com.github.vssavin.usmancore.config.DataSourceSwitcher;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private final DataSourceSwitcher dataSourceSwitcher;

    @Autowired
    UsmanRoutingDatasourceAspect(DataSourceSwitcher dataSourceSwitcher) {
        this.dataSourceSwitcher = dataSourceSwitcher;
    }

    @Around("@annotation(UmRouteDatasource)")
    public Object routeDatasource(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        dataSourceSwitcher.switchToUmDataSource();
        try {
            result = joinPoint.proceed();
        }
        finally {
            dataSourceSwitcher.switchToPreviousDataSource();
        }

        return result;
    }

}
