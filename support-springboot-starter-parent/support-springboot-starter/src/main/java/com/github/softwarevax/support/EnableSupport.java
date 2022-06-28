package com.github.softwarevax.support;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableSupportImportSelector.class)
@Documented
public @interface EnableSupport {


}
