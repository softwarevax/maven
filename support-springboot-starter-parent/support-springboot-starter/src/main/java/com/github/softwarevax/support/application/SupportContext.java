package com.github.softwarevax.support.application;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class SupportContext {

    /**
     * 上下文，用于存元数据
     */
    private Context context;

    public SupportContext() {
        context = new Context();
        initContext();
    }

    /**
     * 初始化的属性
     */
    private void initContext() {
        String launchTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        context.put(PropertyKey.LAUNCH_TIME, launchTime);
    }

    /**
     * 存入值
     * @param key 键
     * @param obj 值
     */
    public void put(String key, Object obj) {
        context.put(key, obj);
    }

    /**
     * 获取值，转换失败排除异常
     * @param key 键
     * @param <T> 值
     * @return 值
     */
    public <T> T get(String key) {
        return context.get(key);
    }
}
