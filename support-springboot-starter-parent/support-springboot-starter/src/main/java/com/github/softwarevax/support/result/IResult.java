package com.github.softwarevax.support.result;

public interface IResult<T> {

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

}
