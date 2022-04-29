package com.github.softwarevax.lock.configuration.annotation;

import com.github.softwarevax.lock.configuration.selector.EnableLockImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author twcao
 * 是否开启
 * 2018/12/6/006 12:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableLockImportSelector.class)
@Documented
public @interface EnableLock {

}
