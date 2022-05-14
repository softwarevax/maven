package com.github.softwarevax.lock.service.impl;

import com.github.softwarevax.lock.aspect.Item;
import com.github.softwarevax.lock.configuration.LockConstant;
import com.github.softwarevax.lock.service.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class DatabaseLockServiceImpl implements LockService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseLockServiceImpl.class);

    private static final String INSERT_SQL = "insert into distribute_lock(lock_key, lock_val, create_time) values(?, ?, ?)";

    private static final String DELETE_SQL = "delete from distribute_lock where lock_key = ?";

    private static final String TRUNCATE_SQL = "delete from distribute_lock";

    private JdbcTemplate template;

    private LockConstant constant;

    private List<Item> keys = new CopyOnWriteArrayList<>();

    public DatabaseLockServiceImpl() {}

    public DatabaseLockServiceImpl(JdbcTemplate template, LockConstant constant) {
        this.template = template;
        this.constant = constant;
        this.template.update(TRUNCATE_SQL);
        new Thread(() -> {
            while (true) {
                try {
                    long sleepTime = 100;
                    if(CollectionUtils.isEmpty(keys)) {
                        sleepTime = constant.getExpired();
                    }
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                    Iterator<Item> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if(System.currentTimeMillis() <= item.getTime()) {
                            continue;
                        }
                        keys.remove(item);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    @Override
    public boolean lock(String lockKey, long timeOut) {
        Assert.hasText(lockKey, "lockKey不能为空");
        Assert.isTrue(timeOut > 0, "timeOut必须大于0");
        try {
            String key = constant.getPrefix() + lockKey;
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            Object[] args = new Object[3];
            args[0] = key;
            args[1] = 1;
            // 时间超过"System.currentTimeMillis() + constant.getExpired()"后，如果还存在该item，即可删除
            keys.add(new Item(lockKey, System.currentTimeMillis() + constant.getExpired()));
            while ((end - start) <= timeOut) {
                args[2] = System.currentTimeMillis();
                try {
                    // sql执行设置1秒超时
                    template.setQueryTimeout(1);
                    if(template.update(INSERT_SQL, args) > 0) {
                        return true;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                TimeUnit.MILLISECONDS.sleep(constant.getRetryInterval());
            }
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean unLock(String lockKey) {
        Assert.hasText(lockKey, "lockKey 不能为空");
        String key = constant.getPrefix() + lockKey;
        Object[] args = new Object[] {key};
        boolean success = template.update(DELETE_SQL, args) > 0;
        if(success) {
            keys.remove(new Item(lockKey, 0));
        }
        return success;
    }
}
