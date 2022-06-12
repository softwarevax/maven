package com.github.softwarevax.dict.core.utils;

import java.lang.reflect.Array;

/**
 * @author ctw
 * 2020/11/21 10:41
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

    public static <T> T[] add(T[] array, T element) {
        Class type;
        if (array != null) {
            type = array.getClass().getComponentType();
        } else {
            if (element == null) {
                throw new IllegalArgumentException("Arguments cannot both be null");
            }

            type = element.getClass();
        }

        T[] newArray = (T[])copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    private static Object copyArrayGrow1(Object array, Class<?> newArrayComponentType) {
        if (array != null) {
            int arrayLength = Array.getLength(array);
            Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        } else {
            return Array.newInstance(newArrayComponentType, 1);
        }
    }
}
