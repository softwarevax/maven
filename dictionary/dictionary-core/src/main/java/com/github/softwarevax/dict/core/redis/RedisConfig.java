package com.github.softwarevax.dict.core.redis;

public class RedisConfig {

    /**
     * 主机
     */
    private String hostName = "127.0.0.1";

    /**
     * 端口
     */
    private int port = 6379;

    /**
     * 密码
     */
    private String password = "";

    /**
     * 数据库索引
     */
    private int database = 0;

    /**
     * key的前缀
     */
    private String prefix = "dict";

    /**
     * 过期时间，默认永不过期(小于等于0则不过其)，单位：秒
     */
    private int expired = 0;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }
}
