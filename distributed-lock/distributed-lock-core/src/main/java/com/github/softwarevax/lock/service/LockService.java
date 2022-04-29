package com.github.softwarevax.lock.service;

public interface LockService {

    boolean lock(String lockKey, long timeOut);

    boolean unLock(String lockKey);
}
