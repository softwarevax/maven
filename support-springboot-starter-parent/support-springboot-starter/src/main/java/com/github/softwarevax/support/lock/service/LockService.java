package com.github.softwarevax.support.lock.service;

public interface LockService {

    boolean lock(String lockKey, long timeOut);

    boolean unLock(String lockKey);
}
