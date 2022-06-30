package com.github.softwarevax.support.configure;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 需要设置线程池的类，可实现此接口
 */
public interface ThreadPoolDemander {

    void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor);

}
