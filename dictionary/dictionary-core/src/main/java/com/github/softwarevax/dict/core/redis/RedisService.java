package com.github.softwarevax.dict.core.redis;

import java.util.List;
import java.util.Map;

public interface RedisService {

    boolean exists(String key);

    boolean set(String key, List<Map<String, Object>> val);

    boolean setex(String key, long expired, List<Map<String, Object>> val);

    boolean setnx(String key, List<Map<String, Object>> val);

    List<Map<String, Object>> get(String key);

    boolean delete(String key);

    RedisConfig config();
}
