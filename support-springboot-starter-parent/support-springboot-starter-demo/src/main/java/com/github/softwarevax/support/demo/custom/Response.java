package com.github.softwarevax.support.demo.custom;

import com.alibaba.fastjson.JSON;
import com.github.softwarevax.support.result.IResult;

public class Response<T> implements IResult {

    private T data;

    @Override
    public String toJSONString(boolean flag, int code, Object obj, String message) {
        return JSON.toJSONString(returnDTO(flag, code, obj, message));
    }

    @Override
    public Response returnDTO(boolean flag, int code, Object obj, String message) {
        Response response = new Response();
        response.setData(obj);
        return response;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
