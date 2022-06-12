package com.github.softwarevax.dict.starter.mybatis.custom;

import com.github.softwarevax.dict.core.interfaces.DictionaryValueComparator;

public class CustomDictionaryValueComparator implements DictionaryValueComparator {

    @Override
    public boolean compare(Object dictValue, Object dbValue) {
        return parseStr(dictValue).equals(parseStr(dbValue));
    }

    private String parseStr(Object obj) {
        return String.valueOf(obj);
    }
}
