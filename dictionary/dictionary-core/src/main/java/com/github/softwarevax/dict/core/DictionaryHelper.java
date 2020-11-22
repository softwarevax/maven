package com.github.softwarevax.dict.core;


import com.github.softwarevax.dict.core.domain.DictionaryConfigure;
import com.github.softwarevax.dict.core.event.AfterRefreshEvent;
import com.github.softwarevax.dict.core.event.DictionaryEvent;
import com.github.softwarevax.dict.core.event.DictionaryEventType;
import com.github.softwarevax.dict.core.interfaces.DictionaryLoader;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.utils.ListUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * 字典表插件管理, 统一处理, 多种字典缓存组合
 */
public class DictionaryHelper {

    public static final Logger logger = Logger.getLogger(DictionaryHelper.class.getName());

    /**
     * key列名
     */
    public static final String KEY_COLUMN = "label";

    /**
     * value列名
     */
    public static final String VALUE_COLUMN = "value";

    /**
     * 缓存来源, database, redis等
     */
    private static List<DictionaryLoader> dictLoaders = new ArrayList<>();

    /**
     * 字典配置
     */
    private static DictionaryConfigure configure = new DictionaryConfigure();

    /**
     * 通知事件
     */
    private static List<DictionaryEvent> events = new ArrayList<>();

    /**
     * 缓存
     */
    private static CacheHolder cacheHolder = new CacheHolder();

    /**
     * 刷新缓存的定时器
     */
    private static Timer timer = new Timer("dict-refresh");

    static {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reLoad();
            }
        }, 0, configure.getRefreshInterval());
        events.add((AfterRefreshEvent) obj -> logger.info("dictionary reload finished"));
    }

    /**
     * 设置配置信息
     * @param configure 属性配置
     */
    public static void configure(DictionaryConfigure configure) {
        DictionaryHelper.configure = configure;
        timer.cancel();
        timer.purge();
        if(!configure.isRefreshEveryTime()) {
            // 定时刷新缓存
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    reLoad();
                }
            }, 0, configure.getRefreshInterval());
        }
    }

    /**
     * 添加缓存来源
     * @param loader 加载器
     */
    public static void addLoader(DictionaryLoader loader) {
        DictionaryHelper.dictLoaders.add(loader);
    }

    /**
     * 加载所有缓存，并组合统一管理, 重新调用刷新所有缓存
     * @return 组合后的所有缓存
     */
    public static void reLoad() {
        notify(events, cacheHolder.cache, DictionaryEventType.BEFORE_REFRESH);
        cacheHolder.clear();
        for(DictionaryLoader loader : dictLoaders) {
            Map<DictionaryTable, List<Map<String, Object>>> loaderCache = loader.reload();
            cacheHolder.putAll(loaderCache);
        }
        notify(events, cacheHolder.cache, DictionaryEventType.AFTER_REFRESH);
    }

    public static void resultWrapper(List<Object> result) {
        if(cacheHolder.size() == 0 || configure.isRefreshEveryTime()) {
            // 加载缓存
            reLoad();
        }
        notify(events, cacheHolder.cache, DictionaryEventType.BEFORE_INVOKE);
        cacheHolder.handleData(result);
        notify(events, cacheHolder.cache, DictionaryEventType.AFTER_INVOKE);
    }

    public static void addListener(DictionaryEvent event) {
        if(events.contains(event)) {
            return;
        }
        events.add(event);
    }

    public static boolean removeListener(DictionaryEvent event) {
        if(!events.contains(event)) {
            return false;
        }
        events.remove(event);
        return true;
    }

    private static void notify(List<DictionaryEvent> events, Object obj, DictionaryEventType type) {
        if(ListUtils.isEmpty(events) || type == null) {
            return;
        }
        for(DictionaryEvent event : events) {
            if(type != event.getEventType()) {
                continue;
            }
            event.callBack(obj);
        }
    }
}
