package com.github.softwarevax.support.result;

import java.util.LinkedHashMap;

public interface IResult {

    /**
     * 将返回结果转字符串
     * @param obj
     * @return 字符串
     */
    String returnString(Object obj);

    /**
     * 将返回结果，包装成DTO
     * @param obj
     * @param <T>
     * @return 实体
     */
    <T> T returnDto(Object obj);

    /**
     * 异常处理
     * @param map 系统返回的异常
     * @param <T> dto包装实体
     * @return 系统返回的异常包装实体
     */
    default <T> T error(LinkedHashMap<String, Object> map) {
        return returnDto(map);
    }
}
