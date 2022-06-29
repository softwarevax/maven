package com.github.softwarevax.support.method.bean;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * @author twcao
 * @title: InvokeMethod
 * @projectName plugin-parent
 * @description: 调用的方法
 * @date 2022/6/29 14:44
 */
public class InvokeMethod implements Cloneable {

    /**
     * 是否暴露为接口（当前方法，是否加了注解，暴露为接口，供外部访问）
     */
    private Boolean expose;

    /**
     * 返回类型 eg: java.lang.String
     */
    private String returnType;

    /**
     * 返回值
     * eg:java.lang.String
     */
    private Object returnObj;

    /**
     * 方法简称
     * eg:queryList
     */
    private String methodName;

    /**
     * 全路径方法名（全限定类名.方法名(参数列表)），可确定唯一性
     * eg:com.github.softwarevax.support.demo.controller.TestController.queryList(java.lang.String)
     */
    private String fullMethodName;

    /**
     * 参数类型，多个之间都好分割
     * eg: java.lang.String,java.lang.String
     */
    private String arg;

    /**
     * 参数名数组：[pageSize,pageNum]
     */
    private String[] args;

    /**
     * 参数值数组（参数类型列表，在fullMethodName中）
     */
    private Object[] argsObj;

    /**
     * 方法所在对象,能被拦截的对象，都是代理对象，不可被序列化的
     */
    private Object invokeObj;

    /**
     * 方法的运行时间，单位：毫秒
     */
    private long elapsedTime;

    /**
     * 接口，expose为true时，interfaces不为空
     */
    private WebInterface interfaces;

    public Boolean getExpose() {
        return expose;
    }

    public void setExpose(Boolean expose) {
        this.expose = expose;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFullMethodName() {
        return fullMethodName;
    }

    public void setFullMethodName(String fullMethodName) {
        this.fullMethodName = fullMethodName;
    }

    public Object[] getArgsObj() {
        return argsObj;
    }

    public void setArgsObj(Object[] argsObj) {
        this.argsObj = argsObj;
    }

    public Object getInvokeObj() {
        return invokeObj;
    }

    public void setInvokeObj(Object invokeObj) {
        this.invokeObj = invokeObj;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public WebInterface getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(WebInterface interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * fullMethodName 可确定唯一方法
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokeMethod that = (InvokeMethod) o;
        return fullMethodName.equals(that.fullMethodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullMethodName);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public InvokeMethod clone() {
        InvokeMethod cloneobj = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(this);
            obs.close();
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            cloneobj = (InvokeMethod) ois.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }
        return cloneobj;
    }
}
