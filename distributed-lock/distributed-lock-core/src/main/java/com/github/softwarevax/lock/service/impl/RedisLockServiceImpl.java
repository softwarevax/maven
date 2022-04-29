package com.github.softwarevax.lock.service.impl;

import com.github.softwarevax.lock.configuration.LockConstant;
import com.github.softwarevax.lock.service.LockService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author twcao
 * @title: LockServiceImpl
 * @projectName plugin-parent
 * @description: redis锁接口实现
 * @date 2022/4/299:51
 */
public class RedisLockServiceImpl implements LockService {

    private RedisTemplate template;

    private LockConstant constant;

    public RedisLockServiceImpl() {}

    public RedisLockServiceImpl(RedisTemplate template, LockConstant constant) {
        this.template = template;
        this.constant = constant;
    }

    /**
     * 获取锁
     * @param lockKey 锁的key
     * @param timeOut 超时时间
     */
    @Override
    public boolean lock(String lockKey, long timeOut) {
        Assert.hasText(lockKey, "lockKey不能为空");
        Assert.isTrue(timeOut > 0, "timeOut必须大于0");
        try {
            String key = constant.getPrefix() + lockKey;
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            while ((end - start) <= timeOut) {
                if(template.opsForValue().setIfAbsent(key, 1, Duration.ofMillis(constant.getExpired()))) {
                    // 如果成功了，直接返回，不等下次重试时间
                    return true;
                }
                TimeUnit.MILLISECONDS.sleep(constant.getRetryInterval());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解锁失败
     * @param lockKey
     */
    @Override
    public boolean unLock(String lockKey) {
        Assert.hasText(lockKey, "lockKey 不能为空");
        String key = constant.getPrefix() + lockKey;
        return template.delete(key);
    }
}
