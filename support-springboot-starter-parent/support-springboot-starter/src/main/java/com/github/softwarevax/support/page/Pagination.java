package com.github.softwarevax.support.page;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pagination {

    String pageSize() default "";

    String pageNum() default "";

    String orderBy() default "";

    int maxPageSize() default 0;
}
