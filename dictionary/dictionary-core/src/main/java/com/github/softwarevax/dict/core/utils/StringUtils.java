package com.github.softwarevax.dict.core.utils;

import java.util.Arrays;

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

    public static boolean equals(final String[] str1, final String[] str2) {
        if(str1 == str2) {
            return true;
        }
        if(str1 == null || str2 == null || str1.length != str2.length) {
            return false;
        }
        String[] copy1 = Arrays.copyOf(str1, str1.length);
        Arrays.sort(copy1);
        String[] copy2 = Arrays.copyOf(str2, str2.length);
        Arrays.sort(copy2);
        return String.join(",", copy1).equals(String.join(",", copy2));
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

    public static String join(CharSequence cs, String ... str) {
        StringBuffer sb = new StringBuffer();
        int idx = 0;
        for (String s : str) {
            sb.append(s);
            idx = sb.length();
            sb.append(cs);
        }
        return sb.toString().substring(0, idx);
    }
}
