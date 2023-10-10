package com.github.softwarevax.dict.core.cache;

import com.github.softwarevax.dict.core.Dict;
import com.github.softwarevax.dict.core.domain.DictionaryEntity;
import com.github.softwarevax.dict.core.enums.DictField;
import com.github.softwarevax.dict.core.interfaces.Comparator;
import com.github.softwarevax.dict.core.interfaces.DictionaryTable;
import com.github.softwarevax.dict.core.interfaces.ValueParser;
import com.github.softwarevax.dict.core.utils.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractCache implements ICache {

    public static final Logger logger = Logger.getLogger(AbstractCache.class.getName());

    /**
     * 缓存解析器
     */
    protected CacheResolver resolver = new CacheResolver(Dict.class);

    protected Comparator comparator;

    protected ValueParser valueParser;

    /**
     * 处理数据
     * @param result 待处理的数据
     */
    @Override
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
            // 目标字段
            Field targetField = FieldUtils.getField(field.getObj().getClass(), propertyName, true);
            // 将对象field.getObj()的属性propertyName设置为dictVal
            Object parserVal = getValueParser().parse(dictVal, targetField.getType());
            BeanUtils.set(field.getObj(), propertyName, parserVal);
        }
        resolveField.clear();
    }

    /**
     * 获取字段的字典
     * @return
     */
    protected Object getColumnCache(DictionaryEntity dict) {
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
                if(!getComparator().compare(cache.get(key), entry.getValue())) {
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
    protected List<Map<String, Object>> getTableCache(String tableName) {
        List<Map<String, Object>> tableCache = new ArrayList<>();
        Map<DictionaryTable, List<Map<String, Object>>> cacheMap = new HashMap<>(this.getCache());
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

    @Override
    public Comparator getComparator() {
        return this.comparator;
    }

    @Override
    public ValueParser getValueParser() {
        return this.valueParser;
    }

    @Override
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    @Override
    public void setValueParser(ValueParser valueParser) {
        this.valueParser = valueParser;
    }
}
