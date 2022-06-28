package com.github.softwarevax.support.result.annotation;

import java.lang.annotation.*;


@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResultWrapper {
}
