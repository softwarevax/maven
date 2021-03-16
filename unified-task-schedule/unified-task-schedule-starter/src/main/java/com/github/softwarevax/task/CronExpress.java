package com.github.softwarevax.task;

import java.lang.annotation.*;

/**
 * @author twcao
 * @description TODO
 * @project unified-task-schedule
 * @classname CronExpress
 * @date 2021/3/16 15:08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CronExpress {
    String value() default "";
}
