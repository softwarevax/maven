package com.github.softwarevax.task.configure;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * @author twcao
 * @description 动态任务注册
 * @project unified-task-schedule
 * @classname JobSchedulingConfigurer
 * @date 2021/3/16 12:21
 */
@Component
public class JobSchedulingConfigurer implements SchedulingConfigurer {

    private ScheduledTaskRegistrar registrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        this.registrar = scheduledTaskRegistrar;
    }

    public ScheduledTaskRegistrar getRegistrar() {
        return registrar;
    }

    public void setRegistrar(ScheduledTaskRegistrar registrar) {
        this.registrar = registrar;
    }
}