package com.github.softwarevax.dict.core.redis;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class RedisServiceImpl implements Closeable, RedisService {

    private RedisClient redisClient;

    private StatefulRedisConnection<String, List> connection;

    private RedisCommands<String, List> commands;

    private RedisConfig config;

    public RedisServiceImpl(RedisConfig config) {
        RedisURI redisUri = RedisURI.builder()
                .withHost(config.getHostName())
                .withPort(config.getPort())
                .withPassword(config.getPassword())
                .withDatabase(config.getDatabase())
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        this.redisClient = RedisClient.create(redisUri);
        this.connection = redisClient.connect(new FastJsonCodec(config.getPrefix()));
        this.commands = connection.sync();
        this.config = config;
    }

    @Override
    public void close() {
        commands.shutdown(true);
        connection.close();
        redisClient.shutdown();
    }

    @Override
    public boolean exists(String key) {
        return this.commands.exists(key) > 0;
    }

    @Override
    public boolean set(String key, List<Map<String, Object>> val) {
        return "OK".equals(this.commands.set(key, val));
    }

    @Override
    public boolean setex(String key, long expired, List<Map<String, Object>> val) {
        return expired <= 0 ? "OK".equals(this.commands.set(key, val)) : "OK".equals(this.commands.setex(key, expired, val));
    }

    @Override
    public boolean setnx(String key, List<Map<String, Object>> val) {
        return "OK".equals(this.commands.setnx(key, val));
    }

    @Override
    public List<Map<String, Object>> get(String key) {
        return this.commands.get(key);
    }

    @Override
    public boolean delete(String key) {
        return this.commands.del(key) > 0;
    }

    @Override
    public RedisConfig config() {
        return this.config;
    }
}
