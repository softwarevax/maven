package com.github.softwarevax.task.bean;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;

/**
 * @author twcao
 * @description 任务操作
 * @project unified-task-schedule
 * @classname JobEntity
 * @date 2021/3/16 13:27
 */
public class JobHandler {

    /**
     * 任务调度器
     */
    ThreadPoolTaskScheduler scheduler;

    /**
     * 待调度的任务
     */
    private ScheduledTask scheduledTask;

    /**
     * 触发任务
     */
    private TriggerTask triggerTask;

    /**
     * 上下文
     */
    private TriggerContext triggerContext;

    public ThreadPoolTaskScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }

    public void setScheduledTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    public TriggerTask getTriggerTask() {
        return triggerTask;
    }

    public void setTriggerTask(TriggerTask triggerTask) {
        this.triggerTask = triggerTask;
    }

    public TriggerContext getTriggerContext() {
        return triggerContext;
    }

    public void setTriggerContext(TriggerContext triggerContext) {
        this.triggerContext = triggerContext;
    }
}
