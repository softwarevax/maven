package com.github.softwarevax.task.configure;

import com.github.softwarevax.task.CronExpress;
import com.github.softwarevax.task.bean.Job;
import com.github.softwarevax.task.bean.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.*;
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
     * 任务容器
     */
    private Map<Job, JobHandler> tasks = new ConcurrentHashMap<>();

    /**
     * 任务注册
     */
    @Autowired
    private JobSchedulingConfigurer register;

    /**
     * 任务调度器
     */
    @Autowired
    ThreadPoolTaskScheduler scheduler;

    /**
     * 新增任务, 自生效
     * @param job 任务实体
     * @return 返回新增的任务
     */
    public Job addJob(Job job) {
        Assert.notNull(job, "job can't be null");
        ScheduledTaskRegistrar registrar = register.getRegistrar();
        registrar.setScheduler(scheduler);
        Runnable runnable = ApplicationContextUtils.register(job.getClazz());
        if(job.getJobId() == null || "".equals(job.getJobId())) {
            job.setJobId(job.getClazz().getName());
        }
        Assert.isNull(this.getJob(job.getJobId()), "任务[" + job.getJobId() + "]已存在");
        if(job.getJobName() == null || "".equals(job.getJobName())) {
            job.setJobName(ClassUtils.getShortName(job.getClazz()));
        }
        CronExpress cron = AnnotationUtils.findAnnotation(job.getClazz(), CronExpress.class);
        if(cron != null && !"".equals(cron.value())) {
            // 注解的属性，大于配置的属性，方便调试
            job.setCron(cron.value());
        }
        job.setEnable(true);
        job.setActive(true);
        JobHandler entity = new JobHandler();
        TriggerTask triggerTask = new TriggerTask(runnable, (TriggerContext triggerContext) -> {
            // 每次任务执行均会进入此方法
            CronTrigger trigger = new CronTrigger(job.getCron());
            entity.setTriggerContext(triggerContext);
            return job.isEnable() ? trigger.nextExecutionTime(triggerContext) : null;
        });
        ScheduledTask scheduledTask = registrar.scheduleTriggerTask(triggerTask);
        entity.setScheduledTask(scheduledTask);
        entity.setTriggerTask(triggerTask);
        entity.setScheduler(scheduler);
        tasks.put(job, entity);
        return job;
    }

    /**
     * 任务类(必须标注了@CronExpress注解，且实现了Runnable接口)
     * @param clazz 接口类
     * @return 任务对象
     */
    public Job addJob(Class<? extends Runnable> clazz) {
        Job job = new Job();
        job.setClazz(clazz);
        return this.addJob(job);
    }

    /**
     * 获取任务操作对象
     * @param jobId 任务id
     * @return 任务操作对象
     */
    public JobHandler getJobHandler(String jobId) {
        return tasks.get(new Job(jobId));
    }

    /**
     * 根据任务id获取任务
     * @param jobId 任务id
     * @return 任务实体
     */
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

    /**
     * 关闭任务(若任务正在执行，待任务执行完)
     * @param jobId 任务id
     * @return 是否关闭成功
     */
    public boolean shutDown(String jobId) {
        try {
            JobHandler handler = this.getJobHandler(jobId);
            Assert.notNull(handler, "任务[" + jobId + "]不存在");
            handler.getScheduledTask().cancel();
            Job job = getJob(jobId);
            job.setActive(false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 启动已经注册的任务
     * @param jobId 任务id
     * @return 是否成功启动
     */
    public boolean startUp(String jobId) {
        try {
            JobHandler handler = this.getJobHandler(jobId);
            Assert.notNull(handler, "任务[" + jobId + "]不存在");
            register.getRegistrar().scheduleTriggerTask(handler.getTriggerTask());
            Job job = getJob(jobId);
            job.setActive(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取所有的任务实体
     * @return
     */
    public List<Job> getJobs() {
        return new ArrayList<>(tasks.keySet());
    }

    /**
     * 删除任务，先关闭再删除
     * @param jobId
     * @return
     */
    public boolean deleteJob(String jobId) {
        try {
            Job job = this.getJob(jobId);
            Assert.notNull(job, "任务[" + jobId + "]不存在");
            shutDown(jobId);
            tasks.remove(job);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
