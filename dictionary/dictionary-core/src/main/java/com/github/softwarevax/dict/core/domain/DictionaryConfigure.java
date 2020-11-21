package com.github.softwarevax.dict.core.domain;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.core
 * @Description: 缓存刷新策略
 * @date 2020/11/21 11:50
 */
public class DictionaryConfigure {

    /**
     * refresh period
     */
    private long refreshInterval = 3600 * 1000;

    /**
     * Whether the cache is flushed before each request
     */
    private boolean refreshEveryTime = false;

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public boolean isRefreshEveryTime() {
        return refreshEveryTime;
    }

    public void setRefreshEveryTime(boolean refreshEveryTime) {
        this.refreshEveryTime = refreshEveryTime;
    }
}
