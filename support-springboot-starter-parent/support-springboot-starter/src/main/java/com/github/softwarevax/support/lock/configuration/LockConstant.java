package com.github.softwarevax.support.lock.configuration;

import com.github.softwarevax.support.lock.configuration.enums.LockEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "support.lock")
public class LockConstant {

    /**
     * 是否启用分布式锁, 默认不启用, 仅当support.lock.enable=true是开启
     */
    private Boolean enable = false;

    /**
     * 锁类型，Redis, Zookeeper, MySQL, Oracle
     */
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

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

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