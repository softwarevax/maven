package com.github.softwarevax.support.method.bean;

public class InvokeStackInfo {

    private String sessionId;

    private long invokeId;

    private MethodInterfaceInvoke interfaceInvoke;

    private Object returnObj;

    public InvokeStackInfo() {}

    public InvokeStackInfo(MethodInterfaceInvoke interfaceInvoke, String sessionId, long invokeId) {
        this.interfaceInvoke = interfaceInvoke;
        this.sessionId = sessionId;
        this.invokeId = invokeId;
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

    public MethodInterfaceInvoke getInterfaceInvoke() {
        return interfaceInvoke;
    }

    public void setInterfaceInvoke(MethodInterfaceInvoke interfaceInvoke) {
        this.interfaceInvoke = interfaceInvoke;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }
}
