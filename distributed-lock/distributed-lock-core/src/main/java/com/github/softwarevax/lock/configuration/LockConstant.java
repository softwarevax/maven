package com.github.softwarevax.lock.configuration;

import com.github.softwarevax.lock.configuration.enums.LockEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lock")
public class LockConstant {

    private LockEnum type;

    /**
     * 获取锁失败后的重试间隔，单位毫秒
     */
    private long retryInterval = 10;

    /**
     * 过期时间
     */
    private long expired = 5000;

    /**
     * 锁前缀
     */
    private String prefix = "";

    public LockEnum getType() {
        return type;
    }

    public void setType(LockEnum type) {
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
}