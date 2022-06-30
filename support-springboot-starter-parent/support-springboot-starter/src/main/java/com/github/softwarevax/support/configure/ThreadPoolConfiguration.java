package com.github.softwarevax.support.configure;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolConfiguration {

    /**
     * 核心线程数
     */
    private int coreSize = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private int maxSize = Runtime.getRuntime().availableProcessors() * 2 + 1;

    /**
     * 队列长度
     */
    private int capacity = 100;

    /**
     * 非核心线程存活时间，单位秒
     */
    private int keepAlive = 300;

    /**
     * 拒绝策略, 默认调用线程运行，不可修改
     */
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * 只有调用时，才初始化
     */
    private ThreadPoolTaskExecutor executor;

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public RejectedExecutionHandler getHandler() {
        return handler;
    }

    public ThreadPoolTaskExecutor threadPoolExecutor() {
        if(executor == null) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(this.getCoreSize());
            threadPoolTaskExecutor.setMaxPoolSize(this.getMaxSize());
            threadPoolTaskExecutor.setQueueCapacity(this.getCapacity());
            threadPoolTaskExecutor.setKeepAliveSeconds(this.getKeepAlive());
            threadPoolTaskExecutor.setRejectedExecutionHandler(this.getHandler());
            threadPoolTaskExecutor.setThreadFactory(new DefaultThreadFactory());
            threadPoolTaskExecutor.initialize();
            return threadPoolTaskExecutor;
        }
        return this.executor;
    }
}
