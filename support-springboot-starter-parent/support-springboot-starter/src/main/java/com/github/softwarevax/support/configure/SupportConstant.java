package com.github.softwarevax.support.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "support")
public class SupportConstant {

    /**
     * 线程池配置，若使用spring默认的线程池，此配置不会生效
     */
    private ThreadPoolConfiguration threadPool;

    public ThreadPoolConfiguration getThreadPool() {
        if(Objects.isNull(threadPool)) {
            threadPool = new ThreadPoolConfiguration();
        }
        return threadPool;
    }

    public void setThreadPool(ThreadPoolConfiguration threadPool) {
        this.threadPool = threadPool;
    }
}
