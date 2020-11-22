package com.github.softwarevax.dict.core.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author ctw
 * 2020/11/22 16:45
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }
}
