package com.github.softwarevax.dict.core.utils;

import com.github.softwarevax.dict.core.Dictionary;
import com.github.softwarevax.dict.core.DictionaryHelper;
import com.github.softwarevax.dict.core.domain.DictionaryEntity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ctw
 * 2020/11/21 11:30
 */
public class DictionaryUtils {

    /**
     * conditionArr[0] a = b
     * conditionArr[1] c = d
     * @param conditionArr 条件数组
     * @return 解析后的条件
     */
    private static Map<String, Object> handleCondition(String[] conditionArr) {
        Map<String, Object> conditionMap = new HashMap<>();
        if(conditionArr == null || conditionArr.length == 0) {
            return conditionMap;
        }
        for(String condition : conditionArr) {
            String[] subCondition = condition.split("=");
            if(subCondition.length != 2) {
                continue;
            }
            conditionMap.put(subCondition[0].trim(), subCondition[1].trim());
        }
        return conditionMap;
    }

    /**
     * 提取字段注解Dictionary的属性值
     * @param field 属性
     * @return 属性上注解的相关值
     */
    public static DictionaryEntity extractDictionaryAnnotation(Field field) {
        Dictionary dictionary = field.getAnnotation(Dictionary.class);
        if(dictionary == null) {
            return null;
        }
        DictionaryEntity dictEntity = new DictionaryEntity();
        // column
        dictEntity.setColumn(StringUtils.isBlank(dictionary.column()) ? DictionaryHelper.KEY_COLUMN : dictionary.column());
        // value
        dictEntity.setValue(StringUtils.isBlank(dictionary.value()) ? DictionaryHelper.VALUE_COLUMN : dictionary.value());
        // conditions
        dictEntity.setCondition(handleCondition(dictionary.conditions()));
        // property
        dictEntity.setProperty(dictionary.property());
        // table
        dictEntity.setTable(dictionary.table());
        return dictEntity;
    }
}
