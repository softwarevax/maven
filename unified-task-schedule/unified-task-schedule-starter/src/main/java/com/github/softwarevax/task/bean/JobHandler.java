package com.github.softwarevax.task.bean;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.TriggerTask;

/**
 * @author twcao
 * @description 任务属性
 * @project unified-task-schedule
 * @classname JobEntity
 * @date 2021/3/16 13:27
 */
public class JobHandler {

    private ScheduledTask scheduledTask;

    private TriggerTask triggerTask;

    private TriggerContext triggerContext;

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
