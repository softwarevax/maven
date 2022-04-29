package com.github.softwarevax.lock.exceptions;

/**
 * @author twcao
 * @title: FetchLockException
 * @projectName plugin-parent
 * @description: 获取锁异常
 * @date 2022/4/299:42
 */
public class FetchLockException extends RuntimeException {

    private String message;

    public FetchLockException(String message) {
        this.message = message;
    }
}
