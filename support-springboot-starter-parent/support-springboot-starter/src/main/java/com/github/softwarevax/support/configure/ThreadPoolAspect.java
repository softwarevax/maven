package com.github.softwarevax.support.configure;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 需要线程池的切面
 */
public interface ThreadPoolAspect {

    void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor);

}
