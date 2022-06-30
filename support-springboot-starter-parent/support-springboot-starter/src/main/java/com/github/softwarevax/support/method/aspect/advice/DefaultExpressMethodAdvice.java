package com.github.softwarevax.support.method.aspect.advice;

import com.github.softwarevax.support.application.PropertyKey;
import com.github.softwarevax.support.application.SupportHolder;
import com.github.softwarevax.support.configure.ThreadPoolDemander;
import com.github.softwarevax.support.method.aspect.MethodInvokeNoticer;
import com.github.softwarevax.support.method.bean.InvokeMethod;
import com.github.softwarevax.support.method.bean.InvokeStackInfo;
import com.github.softwarevax.support.method.bean.MethodInterfaceInvoke;
import com.github.softwarevax.support.method.bean.WebInterface;
import com.github.softwarevax.support.utils.CommonUtils;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.IdWorker;
import com.github.softwarevax.support.utils.StringUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

public class DefaultExpressMethodAdvice implements AbstractExpressMethodAdvice, ThreadPoolDemander {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExpressMethodAdvice.class);

    /**
     * 获取方法参数名实例
     */
    private LocalVariableTableParameterNameDiscoverer parameterNameDiscover = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 需要通知的对象
     */
    private List<MethodInvokeNoticer> noticers = new ArrayList<>();

    /**
     * 计时工具，线程私有
     */
    private ThreadLocal<Stack<StopWatch>> stopWatch = new ThreadLocal<>();

    /**
     * 调用id
     */
    private ThreadLocal<InvokeStackInfo> invoke = new ThreadLocal<>();

    /**
     * 线程池
     */
    private ThreadPoolTaskExecutor executor;

    /**
     * 当前节点的表达式
     */
    private String express;

    @Override
    public void before(MethodInvocation invocation) {
        if(Objects.isNull(stopWatch.get())) {
            Stack<StopWatch> stack = new Stack<>();
            stopWatch.set(stack);
        }
        Stack<StopWatch> stack = stopWatch.get();
        StopWatch watch = new StopWatch();
        watch.start();
        stack.add(watch);
        MethodInterfaceInvoke interfaceInvoke = getMethodInterfaceInvokeFromServlet();
        invoke.set(new InvokeStackInfo(interfaceInvoke, HttpServletUtils.getSessionId(), IdWorker.getId()));
    }

    @Override
    public void afterReturn(Object obj, MethodInvocation invocation) {
        InvokeStackInfo invokeStackInfo = invoke.get();
        invokeStackInfo.setReturnObj(obj);
    }

    @Override
    public void after(MethodInvocation invocation) {
        Stack<StopWatch> stack = stopWatch.get();
        StopWatch watch = stack.pop();
        if(CollectionUtils.isEmpty(this.noticers)) {
            return;
        }
        // 运行时长，单位毫秒
        Assert.notNull(this.executor, "线程池未成功初始化");
        InvokeStackInfo invokeStackInfo = invoke.get();
        executor.execute(() -> {
            // 提取方法中的数据
            InvokeMethod invokeMethod = parseMethod(invocation, invokeStackInfo.getReturnObj());
            invokeMethod.setStartTime(watch.getStartTime());
            invokeMethod.setInvokeId(invokeStackInfo.getInvokeId());
            invokeMethod.setSessionId(invokeStackInfo.getSessionId());
            invokeMethod.setElapsedTime(watch.getTime());
            invokeMethod.setInterfaceInvoke(invokeStackInfo.getInterfaceInvoke());
            noticers.stream().forEach(row -> row.callBack(invokeMethod));
        });
    }

    @Override
    public void throwException(MethodInvocation invocation, Throwable e) {

    }

    @Override
    public String getExpress() {
        return this.express;
    }

    @Override
    public void setExpress(String express) {
        if(StringUtils.isNotBlank(this.express)) {
            return;
        }
        this.express = express;
    }

    @Override
    public void addNoticer(MethodInvokeNoticer noticer) {
        noticers.add(noticer);
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
        SupportHolder holder = SupportHolder.getInstance();
        Map<String, WebInterface> interfaces = holder.getInterfaces();
        if(interfaces.containsKey(fullMethodName)) {
            invokeMethod.setExpose(true);
            invokeMethod.setInterfaces(interfaces.get(fullMethodName));
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
        invokeMethod.setLaunchTime(holder.get(PropertyKey.LAUNCH_TIME));
        invokeMethod.setApplication(holder.get(PropertyKey.APPLICATION_NAME));
        return invokeMethod;
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

    @Override
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }
}
