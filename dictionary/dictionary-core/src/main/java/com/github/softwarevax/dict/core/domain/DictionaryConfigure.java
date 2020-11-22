package com.github.softwarevax.dict.core.domain;

/**
 * @author ctw
 * 缓存刷新策略
 * 2020/11/21 11:50
 */
public class DictionaryConfigure {

    /**
     * refresh period, 单位s, 最快5s刷新一次
     */
    private long refreshInterval = 3600 * 1000;

    /**
     * Whether the cache is flushed before each request
     */
    private boolean refreshEveryTime = false;

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        if(refreshInterval <= 5) {
            return;
        }
        this.refreshInterval = refreshInterval * 1000;
    }

    public boolean isRefreshEveryTime() {
        return refreshEveryTime;
    }

    public void setRefreshEveryTime(boolean refreshEveryTime) {
        this.refreshEveryTime = refreshEveryTime;
    }
}
