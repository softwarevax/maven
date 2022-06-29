package com.github.softwarevax.support.method.aspect;

import com.github.softwarevax.support.method.bean.InvokeMethod;
import com.github.softwarevax.support.method.bean.WebInterface;
import com.github.softwarevax.support.utils.CommonUtils;
import com.github.softwarevax.support.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class MethodInterceptorAdvice implements MethodInterceptor {

    /**
     * 上下文
     */
    private ApplicationContext ctx;

    /**
     * web所有接口
     */
    private Map<String, WebInterface> interfaceMaps = new HashMap<>();

    /**
     * 调用栈, 线程私有
     * 一次请求(一个线程)： controller -> service -> mapper -> service -> controller
     */
    private ThreadLocal<Stack<StopWatch>> threadLocal = new ThreadLocal<>();

    public MethodInterceptorAdvice(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if(Objects.isNull(threadLocal.get())) {
            // 一次请求，第一个进入的是controller
            Stack<StopWatch> stopWatch = new Stack<>();
            threadLocal.set(stopWatch);
        }
        // 获取线程中的方法栈
        Stack<StopWatch> stopWatches = threadLocal.get();
        // 创建-> 启动 -> 进栈
        StopWatch watch = new StopWatch();
        watch.start();
        stopWatches.add(watch);
        Object ret = invocation.proceed();
        // 获取 -> 出栈 -> 关闭
        watch = threadLocal.get().pop();
        watch.stop();
        // 提取方法中的数据
        InvokeMethod invokeMethod = parse(invocation, ret);
        invokeMethod.setElapsedTime(watch.getTotalTimeMillis());
        return ret;
    }

    private InvokeMethod parse(MethodInvocation invocation, Object ret) {
        InvokeMethod invokeMethod = new InvokeMethod();
        Method method = invocation.getMethod();
        // 方法返回值
        String returnType = method.getReturnType().getCanonicalName();
        String fullMethodName = CommonUtils.getMethodName(method);
        invokeMethod.setExpose(interfaceMaps.containsKey(fullMethodName));
        String argument = StringUtils.substring(fullMethodName, StringUtils.indexOf(fullMethodName, "(") + 1, StringUtils.indexOf(fullMethodName, (")")));
        invokeMethod.setArg(argument);
        invokeMethod.setMethodName(method.getName());
        invokeMethod.setFullMethodName(fullMethodName);
        invokeMethod.setReturnObj(ret);
        Object invokeObj = invocation.getThis();
        Object[] arguments = invocation.getArguments();
        invokeMethod.setArgsObj(arguments);
        invokeMethod.setInvokeObj(invokeObj);
        invokeMethod.setReturnType(returnType);
        return invokeMethod;
    }

    public Map<String, WebInterface> getInterfaceMaps() {
        return interfaceMaps;
    }

    public void setInterfaceMaps(Map<String, WebInterface> interfaceMaps) {
        this.interfaceMaps = interfaceMaps;
    }
}
