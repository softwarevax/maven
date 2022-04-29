package com.github.softwarevax.lock.configuration.annotation;

import java.lang.annotation.*;

/**
 * @author twcao
 * @title: Lock
 * @projectName plugin-parent
 * @description: 锁注解
 * @date 2022/4/2910:33
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 保证唯一性，默认是全限定类名 + 方法全称(含参数列表)
     * @return
     */
    String key() default "";

    /**
     * 获取锁超时时间
     * @return
     */
    int timeout() default 5000;
}
