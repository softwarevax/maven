package com.github.softwarevax.dict.core.utils;

/**
 * @author ctw
 * 2020/11/22 12:41
 */
public class Assert {

    public static void isTrue(boolean express, String msg) {
        if(!express) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notNull(Object obj, String msg) {
        if(obj == null) {
            throw new IllegalArgumentException(StringUtils.isBlank(msg) ? "" : msg);
        }
    }

    public static void notNull(Object obj) {
        notNull(obj, null);
    }

    public static <T> void notEmpty(T[] objs, String msg) {
        if(ArrayUtils.isEmpty(objs)) {
            throw new IllegalArgumentException(StringUtils.isBlank(msg) ? "" : msg);
        }
    }

    public static <T> void notEmpty(T[] objs) {
        notEmpty(objs, null);
    }

    public static <T> void hasText(String text, String msg) {
        if(StringUtils.isBlank(text)) {
            throw new IllegalArgumentException(StringUtils.isBlank(msg) ? "" : msg);
        }
    }

    public static <T> void hasText(String text) {
        hasText(text, null);
    }

    public static <T> void hasLength(String text, String msg) {
        hasText(text, msg);
    }

    public static <T> void hasLength(String text) {
        hasText(text, null);
    }
}
