package com.github.softwarevax.task.configure;

import com.github.softwarevax.task.CronExpress;
import com.github.softwarevax.task.bean.Job;
import com.github.softwarevax.task.bean.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author twcao
 * @description 任务调度管理
 * @project unified-task-schedule
 * @classname SchedulerkManager
 * @date 2021/3/16 12:05
 */
@EnableScheduling
@Component
public class SchedulerManager {

    /**
     * 任务实体容器
     */
    private Map<Job, JobHandler> tasks = new ConcurrentHashMap<>();

    /**
     * 任务注册
     */
    @Autowired
    private JobSchedulingConfigurer register;

    public Job put(Job job) {
        Assert.notNull(job, "job can't be null");
        ScheduledTaskRegistrar registrar = register.getRegistrar();
        Runnable runnable = ApplicationContextUtils.register(job.getClazz());
        if(job.getJobId() == null || "".equals(job.getJobId())) {
            job.setJobId(String.valueOf(job.getClass()));
        }
        if(job.getJobName() == null || "".equals(job.getJobName())) {
            job.setJobName(ClassUtils.getShortName(job.getClazz()));
        }
        CronExpress cron = AnnotationUtils.findAnnotation(job.getClazz(), CronExpress.class);
        if(cron != null && !"".equals(cron.value())) {
            // 注解的属性，大于配置的属性，方便调试
            job.setCron(cron.value());
        }
        JobHandler entity = new JobHandler();
        TriggerTask triggerTask = new TriggerTask(runnable, (TriggerContext triggerContext) -> {
            CronTrigger trigger = new CronTrigger(job.getCron());
            entity.setTriggerContext(triggerContext);
            return job.isEnable() ? trigger.nextExecutionTime(triggerContext) : null;
        });
        ScheduledTask scheduledTask = registrar.scheduleTriggerTask(triggerTask);
        entity.setScheduledTask(scheduledTask);
        entity.setTriggerTask(triggerTask);
        tasks.put(job, entity);
        return job;
    }

    public Job put(Class<? extends Runnable> clazz) {
        Job job = new Job();
        job.setClazz(clazz);
        return this.put(job);
    }

    public JobHandler getJobHandler(String jobId) {
        return tasks.get(new Job(jobId));
    }

    public Job getJob(String jobId) {
        Assert.hasText(jobId, "jobId can't be null");
        Set<Job> jobs = tasks.keySet();
        if(jobs.size() == 0) {
            return null;
        }
        Iterator<Job> iterator = jobs.iterator();
        while (iterator.hasNext()) {
            Job next = iterator.next();
            if(jobId.equals(next.getJobId())) {
                return next;
            }
        }
        return null;
    }


}
