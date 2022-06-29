package com.github.softwarevax.support.method.bean;

import java.util.Date;

public class DynamicInfoMethod {

    private int id;

    private String sessionId;

    private long invokeId;

    private int methodId;

    private Boolean expose;

    private String parameterVal;

    private String returnVal;

    private Date startTime;

    private long elapsedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public String getParameterVal() {
        return parameterVal;
    }

    public void setParameterVal(String parameterVal) {
        this.parameterVal = parameterVal;
    }

    public String getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(String returnVal) {
        this.returnVal = returnVal;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Object[] getSQLArgs() {
        Object[] args = new Object[8];
        args[0] = this.sessionId;
        args[1] = this.invokeId;
        args[2] = this.methodId;
        args[3] = this.expose;
        args[4] = this.parameterVal;
        args[5] = this.returnVal;
        args[6] = this.startTime;
        args[7] = this.elapsedTime;
        return args;
    }
}
