package com.github.softwarevax.dict.core.domain;

import com.github.softwarevax.dict.core.cache.DictionaryCache;
import com.github.softwarevax.dict.core.interfaces.Comparator;
import com.github.softwarevax.dict.core.interfaces.ValueParser;
import com.github.softwarevax.dict.core.resolver.DefaultValueComparator;
import com.github.softwarevax.dict.core.resolver.DefaultValueParser;

/**
 * @author ctw
 * 缓存刷新策略
 * 2020/11/21 11:50
 */
public class DictionaryConfigure {

    /**
     * refresh period, 单位s, 最快5s刷新一次, 小于或等于5秒，则使用默认值
     */
    private long refreshInterval = 3600;

    /**
     * Whether the cache is flushed before each request
     */
    private boolean refreshEveryTime = false;

    /**
     * the compare ===> Values retrieved from the dictionary cache are compared to values queried in the database
     * default compare : DefaultValueCompare.class
     */
    private Class<? extends Comparator> comparator = DefaultValueComparator.class;

    /**
     * The result converter matches the data retrieved from the dictionary with the property to be set
     */
    private Class<? extends ValueParser> valueParser = DefaultValueParser.class;

    /**
     * cache configuration
     */
    private DictionaryCache cache = new DictionaryCache();

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
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

    public Class<? extends Comparator> getComparator() {
        return comparator;
    }

    public void setComparator(Class<? extends Comparator> comparator) {
        this.comparator = comparator;
    }

    public Class<? extends ValueParser> getValueParser() {
        return valueParser;
    }

    public void setValueParser(Class<? extends ValueParser> valueParser) {
        this.valueParser = valueParser;
    }

    public DictionaryCache getCache() {
        return cache;
    }

    public void setCache(DictionaryCache cache) {
        this.cache = cache;
    }

    @Override
    public String toString() {
        return "DictionaryConfigure{" +
                "refreshInterval=" + refreshInterval +
                ", refreshEveryTime=" + refreshEveryTime +
                ", comparator=" + comparator +
                ", valueParser=" + valueParser +
                ", cache=" + cache +
                '}';
    }
}
