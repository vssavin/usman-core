package com.github.vssavin.usmancore.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that method must be executed using
 * {@link com.github.vssavin.usmancore.aspect.UsmanRoutingDatasourceAspect} aspect.
 *
 * @author vssavin on 05.12.2023.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UsmanRouteDatasource {

}
