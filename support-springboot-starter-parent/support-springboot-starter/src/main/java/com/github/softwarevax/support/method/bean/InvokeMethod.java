package com.github.softwarevax.support.method.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Objects;

public class InvokeMethod implements Cloneable {

    /**
     * 应用名称, 优先取值ApplicationContext.getApplicationName(),
     * 若为空，则取值ApplicationContext.getId()
     */
    private String application;

    /**
     * 应用的启动时间
     */
    private String launchTime;

    /**
     * HttpSession.Id：会话id，可能为空
     */
    private String sessionId;

    /**
     * 调用id，雪花算法生成的id，用于标记一个调用链路
     * 一次完成调用链路如下：
     * controller -> service -> mapper →
     *                                  ↓
     * controller <- service <- mapper ←
     */
    private long invokeId;

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
     * 参数类型，多个之间都号分割
     * eg: java.lang.String,java.lang.String
     */
    private String arg;

    /**
     * 参数名数组
     * eg: [pageSize,pageNum]
     */
    private String[] args;

    /**
     * 参数值数组（参数类型列表，在fullMethodName中）
     * 与args形成key: value键值对
     */
    private Object[] argsObj;

    /**
     * 方法所在对象,能被拦截的对象，都是代理对象，不可被序列化的
     */
    private Object invokeObj;

    /**
     * 调用当前方法的开始时间：毫秒
     */
    private long startTime;

    /**
     * 方法的运行时间，单位：毫秒
     */
    private long elapsedTime;

    /**
     * 接口，expose为true时，interfaces不为空
     * 封装：RequestMappingInfo、HandlerMethod
     */
    private WebInterface interfaces;

    /**
     * 接口调用的一些动态参数，如：协议、远程ip和端口，请求方法
     */
    private MethodInterfaceInvoke interfaceInvoke;

    /**
     * 方法的注解，如果没有注解，此属性为空，结构如下：
     * <注解类名，<注解属性名， 注解属性名对应的值>>
     * eg：
     * <PostMapping.class, <"name", ["/user/list", "/q2we13"]>>
     */
    private Map<Class, Map<String, Object>> annotations;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

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

    public long getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(String launchTime) {
        this.launchTime = launchTime;
    }

    public MethodInterfaceInvoke getInterfaceInvoke() {
        return interfaceInvoke;
    }

    public void setInterfaceInvoke(MethodInterfaceInvoke interfaceInvoke) {
        this.interfaceInvoke = interfaceInvoke;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<Class, Map<String, Object>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<Class, Map<String, Object>> annotations) {
        this.annotations = annotations;
    }

    /**
     * fullMethodName 可确定唯一方法
     * @param o
     * @return 是否相等
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
