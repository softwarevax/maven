package com.github.softwarevax.support.demo;

import com.alibaba.fastjson.JSON;
import com.github.softwarevax.support.result.IResult;

public class Response<T> implements IResult<Response> {

    private T data;

    @Override
    public String returnString(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public Response returnDto(Object obj) {
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
