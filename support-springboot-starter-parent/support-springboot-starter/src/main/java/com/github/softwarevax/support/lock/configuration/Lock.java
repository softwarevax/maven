package com.github.softwarevax.support.lock.configuration;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    String key() default "";

    int timeout() default 5000;
}
