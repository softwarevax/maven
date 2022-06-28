package com.github.softwarevax.support.result;

public interface IResult<T> {

    /**
     * 将返回结果转字符串
     * @param obj
     * @return
     */
    String returnString(Object obj);

    /**
     * 将返回结果，包装成DTO
     * @param obj
     * @param <T>
     * @return
     */
    <T> T returnDto(Object obj);

}
