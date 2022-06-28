package com.github.softwarevax.support.lock.aspect;

import java.util.Objects;

public class Item {

    private String lockKey;

    private long time;

    public Item() {}

    public Item(String lockKey, long time) {
        this.lockKey = lockKey;
        this.time = time;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return lockKey.equals(item.lockKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockKey);
    }
}
