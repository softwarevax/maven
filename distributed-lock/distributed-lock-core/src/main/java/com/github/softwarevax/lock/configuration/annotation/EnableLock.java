package com.github.softwarevax.lock.configuration.annotation;

import com.github.softwarevax.lock.configuration.selector.EnableLockImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableLockImportSelector.class)
@Documented
public @interface EnableLock {

}
