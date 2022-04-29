package com.github.softwarevax.lock.service;

/**
 * @author twcao
 * @title: LockService
 * @projectName plugin-parent
 * @description: 锁接口
 * @date 2022/4/299:41
 */
public interface LockService {

    /**
     * 获取锁
     * @param lockKey 锁的key
     * @param timeOut 超时时间
     */
    boolean lock(String lockKey, long timeOut);

    /**
     * 解锁失败
     * @param lockKey
     */
    boolean unLock(String lockKey);
}
