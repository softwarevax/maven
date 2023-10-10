package com.github.softwarevax.dict.core.cache;

public enum CacheType {

    MEMORY(com.github.softwarevax.dict.core.cache.LinkedHashMapCache.class), REDIS(com.github.softwarevax.dict.core.cache.RedisCache.class);

    private Class<?> clazz;

    CacheType(Class<?> clazz) {
        this.clazz = clazz;
    }
}
