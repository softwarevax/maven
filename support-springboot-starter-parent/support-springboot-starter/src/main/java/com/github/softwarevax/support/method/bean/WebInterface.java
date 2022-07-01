package com.github.softwarevax.support.method.bean;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class WebInterface {

    private RequestMappingInfo mapping;

    private HandlerMethod method;

    public WebInterface() {
    }

    public WebInterface(RequestMappingInfo mapping, HandlerMethod method) {
        this.mapping = mapping;
        this.method = method;
    }

    public RequestMappingInfo getMapping() {
        return mapping;
    }

    public void setMapping(RequestMappingInfo mapping) {
        this.mapping = mapping;
    }

    public HandlerMethod getMethod() {
        return method;
    }

    public void setMethod(HandlerMethod method) {
        this.method = method;
    }
}
