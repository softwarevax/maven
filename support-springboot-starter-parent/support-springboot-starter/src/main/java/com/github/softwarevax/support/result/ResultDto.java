package com.github.softwarevax.support.result;


import com.alibaba.fastjson.JSON;
import com.github.softwarevax.support.utils.StringUtils;

public class ResultDto<T> implements IResult {


    /**
     * 接口调用返回状态
     */
    public enum Status {
        /**
         * 返回成功或失败的messgae
         */
        SUCCESS(200, "接口调用成功"), FAIL(500, "接口调用失败");

        private String message;

        private int code;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public String message() {
            return this.message;
        }

        public int code() {
            return this.code;
        }
    }

    /**
     * 结果标识
     */
    private boolean flag;

    /**
     * 数据
     */
    private T data;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 状态码
     */
    private int code;

    /**
     * 私有化构造函数
     */
    private ResultDto() {}

    public ResultDto(boolean flag, T data, int code, String message) {
        this.flag = flag;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ResultDto<T> result(boolean flag, T data, int code, String message) {
        ResultDto<T> resultDto = new ResultDto();
        resultDto.flag = flag;
        resultDto.data = data;
        resultDto.code = code;
        resultDto.message = message;
        return resultDto;
    }

    public static <T> ResultDto<T> result(boolean flag, int code, String message) {
        return result(flag, null, code, message);
    }

    public static <T> ResultDto<T> result(boolean flag) {
        Status status = flag ? Status.SUCCESS : Status.FAIL;
        return result(flag, null, status.code, status.message);
    }

    public static <T> ResultDto<T> success(T data, int code, String message) {
        return result(true, data, code, message);
    }

    public static <T> ResultDto<T> success(T data, String message) {
        return result(true, data, Status.SUCCESS.code, message);
    }

    public static <T> ResultDto<T> success(T data) {
        return success(data, Status.SUCCESS.message);
    }

    public static <T> ResultDto<T> success() {
        return success(null, Status.SUCCESS.message);
    }

    public static <T> ResultDto<T> success(String message) {
        return success(null, message);
    }

    public static <T> ResultDto<T> fail(T data, int code, String message) {
        return result(false, data, code, message);
    }

    public static <T> ResultDto<T> fail(T data, String message) {
        return result(false, data, Status.FAIL.code, message);
    }

    public static <T> ResultDto<T> fail(T data) {
        return fail(data, Status.FAIL.message);
    }

    public static <T> ResultDto<T> fail(String message) {
        return fail(null, message);
    }

    public static <T> ResultDto<T> fail() {
        return fail(null, Status.FAIL.message);
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 将返回结果转字符串
     *
     * @param flag
     * @param code
     * @param obj
     * @param message
     * @return 字符串
     */
    @Override
    public String toJSONString(boolean flag, int code, Object obj, String message) {
        return JSON.toJSONString(result(flag, obj, code, message));
    }

    /**
     * 将返回结果，包装成DTO
     *
     * @param flag
     * @param code
     * @param obj
     * @param message
     * @return 实体
     */
    @Override
    public ResultDto<T> returnDTO(boolean flag, int code, Object obj, String message) {
        ResultDto resultDto = new ResultDto<>();
        resultDto.data = obj;
        resultDto.flag = flag;
        resultDto.code = code;
        String msg = flag ? Status.SUCCESS.message : Status.FAIL.message;
        resultDto.message = StringUtils.isBlank(message) ? msg : message;
        return resultDto;
    }
}
