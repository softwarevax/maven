package com.github.softwarevax.dict.core.utils;

import java.util.List;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.utils.collection
 * @Description:
 * @date 2020/11/21 11:47
 */
public class ListUtils {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }
}
