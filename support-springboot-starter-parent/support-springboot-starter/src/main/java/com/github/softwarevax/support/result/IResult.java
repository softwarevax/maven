package com.github.softwarevax.support.result;

public interface IResult {

    /**
     * 将返回结果转字符串
     * @param obj
     * @return 字符串
     */
    String toJSONString(boolean flag, int code, Object obj, String message);

    /**
     * 将返回结果，包装成DTO
     * @param obj
     * @param <T>
     * @return 实体
     */
    <T> T returnDTO(boolean flag, int code, Object obj, String message);
}
