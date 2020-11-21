package com.github.softwarevax.dict.core;


import com.github.softwarevax.dict.core.domain.DictionaryConfigure;
import com.github.softwarevax.dict.core.domain.DictionaryEntity;
import com.github.softwarevax.dict.core.event.DictionaryEvent;
import com.github.softwarevax.dict.core.event.DictionaryEventType;
import com.github.softwarevax.dict.core.interfaces.DictionaryLoader;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.utils.BeanUtils;
import com.github.softwarevax.dict.core.utils.DictionaryUtils;
import com.github.softwarevax.dict.core.utils.ListUtils;
import com.github.softwarevax.dict.core.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 字典表插件管理, 统一处理, 多种字典缓存组合
 */
public class DictionaryHelper {

    /**
     * key列名
     */
    public static final String KEY_COLUMN = "label";

    /**
     * value列名
     */
    public static final String VALUE_COLUMN = "value";

    /**
     * 存放所有的缓存
     */
    private static Map<DictionaryTable, List<Map<String, Object>>> cache = new HashMap<>();

    /**
     * 缓存来源, database, redis等
     */
    private static List<DictionaryLoader> dictLoaders = new ArrayList<>();

    /**
     * 字典缓存配置
     */
    private static DictionaryConfigure configure = new DictionaryConfigure();

    /**
     * 事件
     */
    private static List<DictionaryEvent> events = new ArrayList<>();

    /**
     * 刷新缓存的定时器
     */
    private static Timer timer = new Timer();

    static {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reLoad();
            }
        }, 0, configure.getRefreshInterval());
    }

    /**
     * 设置配置信息
     * @param configure
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
     * @param loader
     */
    public static void addLoader(DictionaryLoader loader) {
        DictionaryHelper.dictLoaders.add(loader);
    }

    /**
     * 加载所有缓存，并组合统一管理, 重新调用刷新所有缓存
     * @return
     */
    public static Map<DictionaryTable, List<Map<String, Object>>> reLoad() {
        notify(events, cache, DictionaryEventType.BEFORE_REFRESH);
        for(DictionaryLoader loader : dictLoaders) {
            Map<DictionaryTable, List<Map<String, Object>>> loaderCache = loader.dictLoader();
            cache.putAll(loaderCache);
        }
        notify(events, cache, DictionaryEventType.AFTER_REFRESH);
        return cache;
    }

    public static void resultWrapper(List<Object> result) {
        if(ListUtils.isEmpty(result)) {
            return;
        }
        if(DictionaryHelper.cache.size() == 0 || configure.isRefreshEveryTime()) {
            // 加载缓存
            reLoad();
        }
        handleDictionary(result);
    }

    private static void handleDictionary(List<Object> result) {
        notify(events, cache, DictionaryEventType.BEFORE_INVOKE);
        Map<Field, DictionaryEntity> markedFieldMap = DictionaryUtils.getMarkedField(result.get(0));
        Collection<Field> markedField = markedFieldMap.keySet();
        for(Object obj : result) {
            for(Field field : markedField) {
                // 获取字段注解的信息
                DictionaryEntity entity = markedFieldMap.get(field);
                // 如果没有配置property，则设置当前属性为缓存查询出的结果
                String propertyName = StringUtils.isBlank(entity.getProperty()) ? field.getName() : entity.getProperty();
                // 属性值, 字典的key[sex] eg: sex:男  ===> sex
                Object propertyVal = BeanUtils.get(obj, field.getName());
                if(propertyVal == null) { // 如果本身没有值，则直接返回
                    continue;
                }
                Map<String, Object> conditions = entity.getCondition();
                conditions.put((String) entity.getValue(), propertyVal);
                Object dictVal = queryCache(entity);
                if(dictVal == null) {
                    continue;
                }
                BeanUtils.set(obj, propertyName, dictVal, field.getType());
            }
        }
        notify(events, cache, DictionaryEventType.AFTER_INVOKE);
    }

    private static Object queryCache(DictionaryEntity dict) {
        if(dict == null) {return null;}
        String tableName = dict.getTable();
        List<Map<String, Object>> propCache = getTableCache(tableName);
        Map<String, Object> conditions = dict.getCondition();
        for(Map<String, Object> cache : propCache) {
            boolean flag = true;
            Iterator<Map.Entry<String, Object>> it = conditions.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                // 如果查不到缓存，跳过
                if(!cache.containsKey(key)) {
                    flag = false;
                    continue;
                }
                if(!cache.get(key).equals(entry.getValue())) {
                    flag = false;
                }
            }
            if(flag) {
               return cache.get(dict.getColumn());
            }
        }
        return null;
    }

    /**
     * 如果tableName不为空，则取表tableName对应的缓存，否则取全部缓存
     * @param tableName
     * @return
     */
    private static List<Map<String, Object>> getTableCache(String tableName) {
        List<Map<String, Object>> tableCache = new ArrayList<>();
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> entry = iterator.next();
            if(StringUtils.isNotBlank(tableName) && tableName.equals(entry.getKey().name())) {
                tableCache.addAll(entry.getValue());
            } else {
                tableCache.addAll(entry.getValue());
            }
        }
        return tableCache;
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
