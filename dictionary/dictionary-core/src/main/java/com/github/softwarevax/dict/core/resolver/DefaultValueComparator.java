package com.github.softwarevax.dict.core.resolver;

import com.github.softwarevax.dict.core.interfaces.DictionaryValueComparator;

public class DefaultValueComparator implements DictionaryValueComparator {

    @Override
    public boolean compare(Object dictValue, Object dbValue) {
        return dictValue.equals(dbValue);
    }

}
