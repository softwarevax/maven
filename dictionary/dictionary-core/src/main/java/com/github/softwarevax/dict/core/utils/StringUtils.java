package com.github.softwarevax.dict.core.utils;

public class StringUtils {

    public static boolean isBlank(String str) {
        if(str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.isEmpty();
    }

    public static String wrapLeft(String str, String wrap) {
        return wrap + str;
    }

    public static String wrapRight(String str, String wrap) {
        return str + wrap;
    }

    public static String wrap(String str, String wrap) {
        return wrap + str + wrap;
    }
}
