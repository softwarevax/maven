package com.github.softwarevax.support.page;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pagination {

    /**
     * @return 页大小的参数名
     */
    String pageSize() default "";

    /**
     * @return 页码参数名
     */
    String pageNum() default "";

    /**
     * @return 排序参数名
     */
    String orderBy() default "";

    /**
     * @return 允许的最大页大小，小于或等于0时，不限制
     */
    int maxPageSize() default 0;

    /**
     * @return 如果没有拿到分页相关参数，是否跳过分页
     * 跳过：即使没拿到，页不会报错，但分页不起效果，此时可当作列表查询使用
     * 不跳过：没拿到参数时，抛出异常
     */
    boolean skipIfMissing() default false;
}
