package com.github.softwarevax.support.method.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.Map;

public class MethodInterfaceInvoke {

    private int id;

    private int invokeId;

    private Serializable userId;

    private String schema;

    private String method;

    private String remoteAddr;

    private Map<String, String> headers;

    private Object payload;

    private int responseStatus;

    private Object responseBody;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    public Serializable getUserId() {
        return userId;
    }

    public void setUserId(Serializable userId) {
        this.userId = userId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Object[] getSQLArgs() {
        Object[] args = new Object[9];
        args[0] = this.invokeId;
        args[1] = this.userId;
        args[2] = this.schema;
        args[3] = this.method;
        args[4] = this.remoteAddr;
        args[5] = JSON.toJSONString(headers);
        args[6] = JSON.toJSONString(this.payload);
        args[7] = JSON.toJSONString(this.responseStatus);
        args[8] = JSON.toJSONString(this.responseBody);
        return args;
    }
}
