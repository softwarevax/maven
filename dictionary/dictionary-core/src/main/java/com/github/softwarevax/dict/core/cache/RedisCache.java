package com.github.softwarevax.dict.core.cache;

import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.redis.RedisService;

import java.util.*;

public class RedisCache extends AbstractCache {

    private List<DictionaryTable> keys;

    private RedisService redisService;

    private int expired;

    public RedisCache(RedisService redisService) {
        this.redisService = redisService;
        this.expired = redisService.config().getExpired();
    }

    @Override
    public void initialize() {
        keys = new ArrayList<>();
    }

    /**
     * 新增一个字段表的缓存
     *
     * @param table
     * @param value
     */
    @Override
    public void put(DictionaryTable table, List<Map<String, Object>> value) {
        keys.add(table);
        redisService.setex(table.toString(), expired, value);
    }

    /**
     * 新增多个字段表的缓存
     *
     * @param cacheMap
     */
    @Override
    public void putAll(Map<DictionaryTable, List<Map<String, Object>>> cacheMap) {
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = cacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> next = iterator.next();
            keys.add(next.getKey());
            redisService.setex(next.getKey().toString(), expired, next.getValue());
        }
    }

    /**
     * 移除某个字典表的缓存
     * @param tables
     */
    @Override
    public void remove(List<DictionaryTable> tables) {
        if(Objects.isNull(tables) || tables.size() == 0) {
            return;
        }
        tables.stream().forEach(row -> {
            keys.remove(row);
            redisService.delete(row.toString());
        });
    }

    /**
     * 清空缓存
     */
    @Override
    public void clear() {
        for (DictionaryTable table : keys) {
            redisService.delete(table.toString());
        }
        keys.clear();
    }

    /**
     * 缓存大小
     */
    @Override
    public int size() {
        return keys.size();
    }

    /**
     * 存放缓存
     */
    @Override
    public Map<DictionaryTable, List<Map<String, Object>>> getCache() {
        Map<DictionaryTable, List<Map<String, Object>>> cache = new HashMap<>();
        for (DictionaryTable table : keys) {
            List<Map<String, Object>> value = (List) redisService.get(table.toString());
            cache.put(table, value);
        }
        return cache;
    }

}
