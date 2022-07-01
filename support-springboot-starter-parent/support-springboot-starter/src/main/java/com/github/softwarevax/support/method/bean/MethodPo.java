package com.github.softwarevax.support.method.bean;

import java.util.Objects;

public class MethodPo {

    private int id;

    private String launchTime;

    private String application;

    private boolean expose;

    private String method;

    private String methodNTag;

    private String fullMethodName;

    private String returnType;

    private String parameter;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFullMethodName() {
        return fullMethodName;
    }

    public void setFullMethodName(String fullMethodName) {
        this.fullMethodName = fullMethodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(String launchTime) {
        this.launchTime = launchTime;
    }

    public boolean isExpose() {
        return expose;
    }

    public void setExpose(boolean expose) {
        this.expose = expose;
    }

    public String getMethodNTag() {
        return methodNTag;
    }

    public void setMethodNTag(String methodNTag) {
        this.methodNTag = methodNTag;
    }

    public Object[] getSQLArgs() {
        Object[] args = new Object[8];
        args[0] = this.application;
        args[1] = this.launchTime;
        args[2] = this.expose;
        args[3] = this.method;
        args[4] = this.methodNTag;
        args[5] = this.fullMethodName;
        args[6] = this.returnType;
        args[7] = this.parameter;
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodPo methodPo = (MethodPo) o;
        return launchTime.equals(methodPo.launchTime) && fullMethodName.equals(methodPo.fullMethodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(launchTime, fullMethodName);
    }
}
