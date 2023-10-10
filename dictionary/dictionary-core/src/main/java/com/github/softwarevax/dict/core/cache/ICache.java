package com.github.softwarevax.dict.core.cache;

import com.github.softwarevax.dict.core.interfaces.Comparator;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.interfaces.ValueParser;

import java.util.List;
import java.util.Map;

public interface ICache {

    void initialize();

    /**
     * 新增一个字段表的缓存
     */
    void put(DictionaryTable table, List<Map<String, Object>> value);

    /**
     * 新增多个字段表的缓存
     */
    void putAll(Map<DictionaryTable, List<Map<String, Object>>> cacheMap);

    /**
     * 移除某个字典表的缓存
     */
    void remove(List<DictionaryTable> tables);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 处理数据
     */
    void handleData(List<Object> result);

    /**
     缓存大小
     */
    int size();

    /**
     * 存放缓存
     */
    Map<DictionaryTable, List<Map<String, Object>>> getCache();

    /**
     * 比较器，匹配业务表和字典表值，可自定义
     */
    Comparator getComparator();

    void setComparator(Comparator comparator);

    /**
     * 转换器，将字典中的字段与属性中的字段匹配
     * 解决问题：典类型是Integer类型，反显后的值是String,那么类型转化失败
     */
    ValueParser getValueParser();

    void setValueParser(ValueParser valueParser);
}
