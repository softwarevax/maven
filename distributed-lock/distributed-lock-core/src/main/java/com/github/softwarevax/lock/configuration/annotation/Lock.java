package com.github.softwarevax.lock.configuration.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    String key() default "";

    int timeout() default 5000;
}
