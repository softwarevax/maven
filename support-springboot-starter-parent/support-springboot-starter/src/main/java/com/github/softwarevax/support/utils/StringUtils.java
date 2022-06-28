package com.github.softwarevax.support.utils;

import org.springframework.util.CollectionUtils;

import java.util.*;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    public static List<String> splitToList(String str, String split) {
        if(StringUtils.isAnyBlank(str, split)) {
            return new ArrayList<>();
        }
        String[] array = split(str, split);
        return Arrays.asList(array);
    }

    public static Map<String, String> splitToMap(List<String> str, String split) {
        Map<String, String> result = new HashMap<>();
        if(CollectionUtils.isEmpty(str)) {
            return result;
        }
        str.stream().forEach(string -> {
            String[] array = split(string, split, 2);
            result.put(array[0], array.length == 2 ? array[1] : null);
        });
        return result;
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
}
