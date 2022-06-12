package com.github.softwarevax.dict.core.domain;

import com.github.softwarevax.dict.core.interfaces.DictionaryValueComparator;
import com.github.softwarevax.dict.core.interfaces.DictionaryValueParser;
import com.github.softwarevax.dict.core.resolver.DefaultDictionaryValueParser;
import com.github.softwarevax.dict.core.resolver.DefaultValueComparator;

/**
 * @author ctw
 * 缓存刷新策略
 * 2020/11/21 11:50
 */
public class DictionaryConfigure {

    /**
     * refresh period, 单位s, 最快5s刷新一次, 小于或等于5秒，则使用默认值
     */
    private long refreshInterval = 3600 * 1000;

    /**
     * Whether the cache is flushed before each request
     */
    private boolean refreshEveryTime = false;

    /**
     * the compare ===> Values retrieved from the dictionary cache are compared to values queried in the database
     * default compare : DefaultValueCompare.class
     */
    private Class<? extends DictionaryValueComparator> comparator = DefaultValueComparator.class;

    /**
     * The result converter matches the data retrieved from the dictionary with the property to be set
     */
    private Class<? extends DictionaryValueParser> valueParser = DefaultDictionaryValueParser.class;

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

    public Class<? extends DictionaryValueComparator> getComparator() {
        return comparator;
    }

    public void setComparator(Class<? extends DictionaryValueComparator> comparator) {
        this.comparator = comparator;
    }

    public Class<? extends DictionaryValueParser> getValueParser() {
        return valueParser;
    }

    public void setValueParser(Class<? extends DictionaryValueParser> valueParser) {
        this.valueParser = valueParser;
    }

    @Override
    public String toString() {
        return "\r\nrefreshInterval=" + refreshInterval +
                ", \r\nrefreshEveryTime=" + refreshEveryTime +
                ", \r\ncomparator=" + comparator +
                ", \r\nvalueParser=" + valueParser;
    }
}
