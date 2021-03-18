package com.github.softwarevax.task.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author twcao
 * @description 动态任务配置
 * @project unified-task-schedule
 * @classname DynamicTaskConfiguration
 * @date 2021/3/16 14:57
 */
@ComponentScan(basePackages = {"com.github.softwarevax.task"})
public class DynamicTaskConfiguration {

    /**
     * 任务调度器
     * @return
     */
    @Bean
    @Primary
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 设置线程前缀
        scheduler.setThreadNamePrefix("unified-schedule-task-");
        return scheduler;
    }
}
