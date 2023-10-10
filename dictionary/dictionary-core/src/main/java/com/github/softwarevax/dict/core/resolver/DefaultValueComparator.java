package com.github.softwarevax.dict.core.resolver;

import com.github.softwarevax.dict.core.interfaces.Comparator;

public class DefaultValueComparator implements Comparator {

    @Override
    public boolean compare(Object dictValue, Object dbValue) {
        return dictValue.equals(dbValue);
    }

}
