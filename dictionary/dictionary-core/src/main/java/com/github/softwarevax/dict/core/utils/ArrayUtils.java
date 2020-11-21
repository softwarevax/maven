package com.github.softwarevax.dict.core.utils;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.utils.collection
 * @Description:
 * @date 2020/11/21 10:41
 */
public class ArrayUtils {

    public static <T> int length(T[] strs) {
        return strs == null || strs.length == 0 ? 0 : strs.length;
    }

    public static <T> boolean isEmpty(T[] strs) {
        return length(strs) == 0;
    }

    public static <T>boolean isNotEmpty(T[] strs) {
        return length(strs) != 0;
    }

    public static String[] wrap(String[] strs, String wrap) {
        if(isEmpty(strs)) {
            return strs;
        }
        for(String str : strs) {
            str = StringUtils.wrap(str, wrap);
        }
        return strs;
    }

    public static String[] trim(String[] strs) {
        if(isEmpty(strs)) {
            return strs;
        }
        for(String str : strs) {
            if(StringUtils.isBlank(str)) {
                continue;
            }
            str = str.trim();
        }
        return strs;
    }

    public static boolean equals(String str1, String str2) {
        if(str1 == str2) {
            return true;
        }
        if(str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean contains(String[] strs, String s) {
        if(isEmpty(strs) || StringUtils.isBlank(s)) {
            return false;
        }
        for(String str : strs) {
            if(equals(str, s)) {
                return true;
            }
        }
        return false;
    }
}
