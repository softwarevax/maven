package com.github.softwarevax.task.bean;

import java.util.Objects;

/**
 * @author twcao
 * @description 任务实体
 * @project unified-task-schedule
 * @classname Job
 * @date 2021/3/16 11:59
 */
public class Job {

    /**
     * 任务id, 用于标识，默认使用全限定类名
     */
    private String jobId;

    /**
     * 任务名称, 默认简单类名
     */
    private String jobName;

    /**
     * cron表达式, 修改后即可生效
     */
    private String cron;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 是否启用, 默认启用, 修改后即可生效
     */
    private boolean enable = true;

    /**
     * 任务运行类
     */
    private Class<? extends Runnable> clazz;

    public Job() {
    }

    public Job(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Class<? extends Runnable> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Runnable> clazz) {
        this.clazz = clazz;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return jobId.equals(job.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }
}
