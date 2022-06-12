package com.github.softwarevax.dict.core.resolver;

import com.github.softwarevax.dict.core.interfaces.DictionaryValueParser;
import com.github.softwarevax.dict.core.resolver.gsetter.Getter;

public class DefaultDictionaryValueParser implements DictionaryValueParser {

    @Override
    public <T> T parse(Object val, Class<T> clazz) {
        // 字典字段的类型
        String targetClazz = clazz.getSimpleName();
        if(val.getClass() == clazz.getClass()) {
            return (T) val;
        }
        switch (targetClazz) {
            // 如果目标类型是字符串
            case "String":
                return (T)Getter.getString(val);
            case "Boolean":
                return (T)Getter.getBoolean(val);
        }
        return null;
    }
}
