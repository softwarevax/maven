package com.github.softwarevax.dict.core.interfaces;

public interface ValueParser {

    /**
     * 字典值的转换
     * @param val 字典值
     * @param clazz 目标字段的类型
     * @param <T> 目标值
     * @return 字典根据目标值转换后的值
     */
    <T> T parse(Object val, Class<T> clazz);
}
