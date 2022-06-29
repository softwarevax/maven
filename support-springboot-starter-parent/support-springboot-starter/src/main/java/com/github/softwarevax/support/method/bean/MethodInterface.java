package com.github.softwarevax.support.method.bean;

public class MethodInterface {

    /**
     * 主键
     */
    private int id;

    /**
     * 接口对应的方法id
     */
    private int methodId;

    /**
     * 请求方法类型，get，post,delete...
     */
    private String method;

    /**
     * 接口路径，不含contextPath
     */
    private String mappings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMappings() {
        return mappings;
    }

    public void setMappings(String mappings) {
        this.mappings = mappings;
    }

    public Object[] getSQLArgs() {
        Object[] args = new Object[3];
        args[0] = this.methodId;
        args[1] = this.method;
        args[2] = this.mappings;
        return args;
    }
}
