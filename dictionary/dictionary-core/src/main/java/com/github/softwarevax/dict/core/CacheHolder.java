package com.github.softwarevax.dict.core;

import com.github.softwarevax.dict.core.domain.DictionaryEntity;
import com.github.softwarevax.dict.core.enums.DictField;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.interfaces.DictionaryValueComparator;
import com.github.softwarevax.dict.core.interfaces.DictionaryValueParser;
import com.github.softwarevax.dict.core.utils.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * @author ctw
 * 缓存
 * 2020/11/22 13:19
 */
public class CacheHolder {

    public static final Logger logger = Logger.getLogger(CacheHolder.class.getName());

    /**
     * 字典缓存
     */
    Map<DictionaryTable, List<Map<String, Object>>> cache = new LinkedHashMap<>();

    /**
     * 缓存解析器
     */
    private CacheResolver resolver = new CacheResolver(Dictionary.class);

    /**
     * 安全锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 比较器，匹配业务表和字典表值，可自定义
     */
    private DictionaryValueComparator comparator;

    /**
     * 转换器，将字典中的字段与属性中的字段匹配
     * 解决问题：典类型是Integer类型，反显后的值是String,那么类型转化失败
     */
    private DictionaryValueParser valueParser;

    public CacheHolder(Class<? extends DictionaryValueComparator> comparator, Class<? extends DictionaryValueParser> valueParser) {
        Assert.isTrue(!comparator.isInterface(), "comparator不能是接口");
        this.comparator = BeanUtils.newInstance(comparator);
        this.valueParser = BeanUtils.newInstance(valueParser);
    }

    public void put(DictionaryTable table, List<Map<String, Object>> value) {
        try {
            lock.lock();
            cache.put(table, value);
        } finally {
            lock.unlock();
        }
    }

    public void putAll(Map<DictionaryTable, List<Map<String, Object>>> cacheMap) {
        try {
            lock.lock();
            cache.putAll(cacheMap);
        } finally {
            lock.unlock();
        }
    }

    public void remove(Map<DictionaryTable, List<Map<String, Object>>> cacheMap) {
        try {
            lock.lock();
            cache.putAll(cacheMap);
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lock();
            this.cache.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 处理数据
     * @param result 待处理的数据
     */
    public void handleData(List<Object> result) {
        if(ListUtils.isEmpty(result)) {
            return;
        }
        List<DictField> resolveField = resolver.resolve(result);
        for(DictField field : resolveField) {
            // 获取字段注解的信息
            DictionaryEntity entity = DictionaryUtils.extractDictionaryAnnotation(field.getField());
            // 如果没有配置property，则设置当前属性为缓存查询出的结果
            String propertyName = StringUtils.isBlank(entity.getProperty()) ? field.getField().getName() : entity.getProperty();
            // 属性值, 字典的key[sex] eg: sex:男  ===> sex
            Object propertyVal = BeanUtils.get(field.getObj(), field.getField().getName());
            if(propertyVal == null) { // 如果本身没有值，则直接返回
                continue;
            }
            Map<String, Object> conditions = entity.getCondition();
            conditions.put((String) entity.getValue(), propertyVal);
            Object dictVal = getColumnCache(entity);
            if(dictVal == null) {
                continue;
            }
            // 将对象field.getObj()的属性propertyName设置为dictVal
            Object parserVal = valueParser.parse(dictVal, field.getField().getType());
            BeanUtils.set(field.getObj(), propertyName, parserVal);
        }
        resolveField.clear();
    }

    /**
     * 获取字段的字典
     * @return
     */
    private Object getColumnCache(DictionaryEntity dict) {
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
                // 字典缓存中取出的值 比较 数据库中查询的值 fixup: 适配:"1" == 1
                if(!comparator.compare(cache.get(key), entry.getValue())) {
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
     * 查询一个表的字典
     * 如果tableName不为空，则取表tableName对应的缓存，否则取全部表的缓存
     * @param tableName 表名
     * @return 表字典
     */
    private List<Map<String, Object>> getTableCache(String tableName) {
        List<Map<String, Object>> tableCache = new ArrayList<>();
        Map<DictionaryTable, List<Map<String, Object>>> cacheMap = new HashMap<>(this.cache);
        Iterator<Map.Entry<DictionaryTable, List<Map<String, Object>>>> iterator = cacheMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DictionaryTable, List<Map<String, Object>>> entry = iterator.next();
            if(StringUtils.isNotBlank(tableName) && tableName.equals(entry.getKey().name())) {
                tableCache.addAll(entry.getValue());
            } else {
                tableCache.addAll(entry.getValue());
            }
        }
        cacheMap.clear();
        return tableCache;
    }

    public int size() {
        try {
            lock.lock();
            return this.cache.size();
        } finally {
            lock.unlock();
        }
    }
}
