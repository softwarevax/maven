package com.github.softwarevax.dict.core.cache;

import com.github.softwarevax.dict.core.redis.RedisConfig;

public class DictionaryCache {

    private CacheType type = CacheType.MEMORY;

    private RedisConfig redis = new RedisConfig();

    public CacheType getType() {
        return type;
    }

    public void setType(CacheType type) {
        this.type = type;
    }

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }
}
