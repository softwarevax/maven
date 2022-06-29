package com.github.softwarevax.support.method.aspect;

import com.github.softwarevax.support.configure.ThreadPoolAspect;
import com.github.softwarevax.support.method.bean.InvokeMethod;
import com.github.softwarevax.support.method.bean.InvokeStackInfo;
import com.github.softwarevax.support.method.bean.MethodInterfaceInvoke;
import com.github.softwarevax.support.method.bean.WebInterface;
import com.github.softwarevax.support.method.configuration.MethodConstant;
import com.github.softwarevax.support.utils.CommonUtils;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.IdWorker;
import com.github.softwarevax.support.utils.StringUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;

public class MethodInterceptorAdvice implements MethodInterceptor, ThreadPoolAspect {

    /**
     * 上下文
     */
    private ApplicationContext ctx;

    /**
     * 调用栈, 线程私有
     * 一次请求(一个线程)： controller -> service -> mapper -> service -> controller
     */
    private ThreadLocal<Stack<StopWatch>> threadLocal = new ThreadLocal<>();

    /**
     * 调用id
     */
    private ThreadLocal<InvokeStackInfo> linkThreadLocal = new ThreadLocal<>();

    /**
     * web所有接口
     */
    private Map<String, WebInterface> interfaceMaps = new HashMap<>();

    /**
     * 线程池
     */
    private ThreadPoolTaskExecutor executor;

    /**
     * 监听器
     */
    private List<MethodListener> listeners = new ArrayList<>();

    /**
     * 获取参数名
     */
    private static LocalVariableTableParameterNameDiscoverer parameterNameDiscover = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 启动时间
     */
    private String launchTime;

    public MethodInterceptorAdvice(ApplicationContext ctx, List<Class<? extends MethodListener>> listenerClazz) {
        this.ctx = ctx;
        this.launchTime = DateFormatUtils.format(ctx.getStartupDate(), "yyyyMMddHHmmss");
        MethodConstant methodConstant = ctx.getBean(MethodConstant.class);
        if(methodConstant.getPersistence()) {
            persistence();
        }
        listenerClazz.stream().forEach(row -> listeners.add(BeanUtils.instantiateClass(row)));
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if(Objects.isNull(threadLocal.get())) {
            // 一次请求，第一个进入的是controller
            Stack<StopWatch> stopWatch = new Stack<>();
            threadLocal.set(stopWatch);
            MethodInterfaceInvoke interfaceInvoke = getMethodInterfaceInvokeFromServlet();
            // 设置链路信息
            linkThreadLocal.set(new InvokeStackInfo(interfaceInvoke, HttpServletUtils.getSessionId(), IdWorker.getId()));
        }
        // 获取线程中的方法栈
        Stack<StopWatch> stopWatches = threadLocal.get();
        // 创建-> 启动 -> 进栈
        StopWatch watch = new StopWatch();
        watch.start();
        stopWatches.add(watch);
        Object ret = null;
        try {
            ret = invocation.proceed();
        } catch (Throwable e) {
            // 如果抛出了异常，则先执行完finally中的计时和请求解析，然后将异常原样抛出
            throw e;
        } finally {
            // 获取 -> 出栈 -> 关闭
            watch = threadLocal.get().pop();
            watch.stop();
            Object innerObj = ret;
            long startTime = watch.getStartTime();
            long elapsedTime = watch.getTime();
            InvokeStackInfo invokeStackInfo = linkThreadLocal.get();
            MethodInterfaceInvoke interfaceInvoke = invokeStackInfo.getInterfaceInvoke();
            if(!Objects.isNull(executor)) {
                executor.execute(() -> {
                    // 提取方法中的数据
                    InvokeMethod invokeMethod = parseMethod(invocation, innerObj);
                    invokeMethod.setStartTime(startTime);
                    invokeMethod.setInvokeId(invokeStackInfo.getInvokeId());
                    invokeMethod.setSessionId(invokeStackInfo.getSessionId());
                    invokeMethod.setElapsedTime(elapsedTime);
                    invokeMethod.setInterfaceInvoke(interfaceInvoke);
                    listeners.stream().forEach(row -> row.callBack(invokeMethod));
                });
            }
        }
        return ret;
    }

    /**
     * 解析方法和请求
     * @param invocation
     * @param ret
     * @return
     */
    private InvokeMethod parseMethod(MethodInvocation invocation, Object ret) {
        InvokeMethod invokeMethod = new InvokeMethod();
        Method method = invocation.getMethod();
        // 方法返回值
        String returnType = method.getReturnType().getCanonicalName();
        String fullMethodName = CommonUtils.getMethodName(method);
        invokeMethod.setExpose(false);
        if(interfaceMaps.containsKey(fullMethodName)) {
            invokeMethod.setExpose(true);
            invokeMethod.setInterfaces(interfaceMaps.get(fullMethodName));
        }
        String argument = StringUtils.substring(fullMethodName, StringUtils.indexOf(fullMethodName, "(") + 1, StringUtils.indexOf(fullMethodName, (")")));
        invokeMethod.setArgs(parameterNameDiscover.getParameterNames(method));
        invokeMethod.setArg(argument);
        invokeMethod.setMethodName(method.getName());
        invokeMethod.setFullMethodName(fullMethodName);
        invokeMethod.setReturnObj(ret);
        Object invokeObj = invocation.getThis();
        Object[] arguments = invocation.getArguments();
        invokeMethod.setArgsObj(arguments);
        invokeMethod.setInvokeObj(invokeObj);
        invokeMethod.setReturnType(returnType);
        invokeMethod.setLaunchTime(this.launchTime);
        invokeMethod.setApplication(ctx.getId());
        return invokeMethod;
    }

    public Map<String, WebInterface> getInterfaceMaps() {
        return interfaceMaps;
    }

    public void setInterfaceMaps(Map<String, WebInterface> interfaceMaps) {
        this.interfaceMaps = interfaceMaps;
    }

    @Override
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * 持久化
     * @return
     */
    private boolean persistence() {
        // 用于持久化
        DefaultMethodListener defaultMethodListener = BeanUtils.instantiateClass(DefaultMethodListener.class);
        JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
        Assert.notNull(template, "无可用的数据源");
        defaultMethodListener.setTemplate(template);
        Assert.isTrue(defaultMethodListener.checkTable(), "表不存在");
        listeners.add(defaultMethodListener);
        return true;
    }

    /**
     * 获取请求中的信息
     * @return
     */
    private MethodInterfaceInvoke getMethodInterfaceInvokeFromServlet() {
        MethodInterfaceInvoke interfaceInvoke = new MethodInterfaceInvoke();
        interfaceInvoke.setMethod(HttpServletUtils.getMethod());
        interfaceInvoke.setHeaders(HttpServletUtils.getHeaders());
        interfaceInvoke.setRemoteAddr(HttpServletUtils.remoteAddress());
        interfaceInvoke.setSchema(HttpServletUtils.getSchema());
        interfaceInvoke.setResponseStatus(HttpServletUtils.getResponseStatus());
        return interfaceInvoke;
    }
}
