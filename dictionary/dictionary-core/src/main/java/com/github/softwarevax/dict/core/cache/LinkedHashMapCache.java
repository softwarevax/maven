package com.github.softwarevax.dict.core.cache;

import com.github.softwarevax.dict.core.interfaces.DictionaryTable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ctw
 * 缓存
 * 2020/11/22 13:19
 */
public class LinkedHashMapCache extends AbstractCache {

    /**
     * 字典缓存
     */
    private Map<DictionaryTable, List<Map<String, Object>>> cache;

    /**
     * 安全锁
     */
    private ReentrantLock lock;

    @Override
    public void initialize() {
        cache = new LinkedHashMap<>();
        lock = new ReentrantLock();
    }

    @Override
    public Map<DictionaryTable, List<Map<String, Object>>> getCache() {
        return this.cache;
    }

    @Override
    public void put(DictionaryTable table, List<Map<String, Object>> value) {
        try {
            lock.lock();
            getCache().put(table, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(Map<DictionaryTable, List<Map<String, Object>>> cacheMap) {
        try {
            lock.lock();
            getCache().putAll(cacheMap);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(List<DictionaryTable> tables) {
        if(Objects.isNull(tables) || tables.size() == 0) {
            return;
        }
        try {
            lock.lock();
            tables.stream().forEach(row -> getCache().remove(row));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            getCache().clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        try {
            lock.lock();
            return this.getCache().size();
        } finally {
            lock.unlock();
        }
    }
}
