package com.github.softwarevax.support.method.aspect.advice;

import com.github.softwarevax.support.application.PropertyKey;
import com.github.softwarevax.support.application.SupportHolder;
import com.github.softwarevax.support.configure.ThreadPoolDemander;
import com.github.softwarevax.support.method.aspect.MethodInvokeNoticer;
import com.github.softwarevax.support.method.bean.*;
import com.github.softwarevax.support.utils.CommonUtils;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.IdWorker;
import com.github.softwarevax.support.utils.StringUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

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
     * 调用栈
     */
    private ThreadLocal<InvokeStackInfo> invoke = new ThreadLocal<>();

    /**
     * 调用id
     */
    private ThreadLocal<Long> invokeIdThread = new ThreadLocal<>();

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
        // 1、获取接口调用信息
        MethodInterfaceInvoke interfaceInvoke = getMethodInterfaceInvokeFromServlet();
        // 2、调用的id，同一次请求的id，共同一个invokeId
        if(Objects.isNull(invokeIdThread.get())) {
            Long invokeId = IdWorker.getId();
            invokeIdThread.set(invokeId);
        }
        // 3、构造调用栈，controller -> service -> mapper -> service -> controller
        InvokeStackInfo invokeStackInfo = new InvokeStackInfo(interfaceInvoke, HttpServletUtils.getSessionId(), invokeIdThread.get());
        invoke.set(invokeStackInfo);
    }

    @Override
    public void afterReturn(Object obj, MethodInvocation invocation) {
        // 4、将方法的返回结果，放入调用栈，后面统一处理
        InvokeStackInfo invokeStackInfo = invoke.get();
        invokeStackInfo.setReturnObj(obj);
        // 5、处理数据，并考虑是否入库
        Stack<StopWatch> stack = stopWatch.get();
        StopWatch watch = stack.pop();
        if(CollectionUtils.isEmpty(this.noticers)) {
            return;
        }
        // 避免放在线程池中等待的时间，计算到运行时间中
        long elapsedTime = watch.getTime();
        // 运行时长，单位毫秒
        Assert.notNull(this.executor, "线程池未成功初始化");
        // 提取方法和请求中的数据
        InvokeMethod invokeMethod = parseMethod(invocation, invokeStackInfo.getReturnObj());
        invokeMethod.setStartTime(watch.getStartTime());
        invokeMethod.setInvokeId(invokeStackInfo.getInvokeId());
        // 如果不是请求方法，sessionId则为空
        invokeMethod.setSessionId(invokeStackInfo.getSessionId());
        invokeMethod.setElapsedTime(elapsedTime);
        invokeMethod.setInterfaceInvoke(invokeStackInfo.getInterfaceInvoke());
        executor.execute(() -> noticers.stream().forEach(row -> row.callBack(invokeMethod)));
    }

    @Override
    public void after(MethodInvocation invocation) {
        Stack<StopWatch> stack = stopWatch.get();
        // 如果计时器都被取完了，说明调用栈已经执行到最后异一步了，则将线程中的结果清楚
        if(stack.isEmpty()) {
            invoke.remove();
            invokeIdThread.remove();
        }
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
     * @param invocation 调用的方法
     * @param ret 方法返回值
     * @return
     */
    private InvokeMethod parseMethod(MethodInvocation invocation, Object ret) {
        InvokeMethod invokeMethod = new InvokeMethod();
        Method method = invocation.getMethod();
        Map<Class, Map<String, Object>> methodAnnotation = getMethodAnnotation(method);
        invokeMethod.setAnnotations(methodAnnotation);
        // 方法返回值
        String returnType = method.getReturnType().getCanonicalName();
        String fullMethodName = CommonUtils.getMethodName(method);
        invokeMethod.setExpose(false);
        SupportHolder holder = SupportHolder.getInstance();
        Map<String, WebInterface> interfaces = holder.getInterfaces();
        if(interfaces.containsKey(fullMethodName)) {
            // 判断是否为接口
            invokeMethod.setExpose(true);
            invokeMethod.setInterfaces(interfaces.get(fullMethodName));
        }
        String argument = StringUtils.substring(fullMethodName, StringUtils.indexOf(fullMethodName, "(") + 1, StringUtils.indexOf(fullMethodName, (")")));
        // 解析请求的参数
        if(method.getDeclaringClass().isInterface()) {
            Parameter[] parameters = method.getParameters();
            String[] args = Arrays.asList(parameters).stream().map(Parameter::getName).toArray(String[]::new);
            invokeMethod.setArgs(args);
        } else {
            invokeMethod.setArgs(parameterNameDiscover.getParameterNames(method));
        }
        if(ArrayUtils.getLength(invokeMethod.getArgs()) == 0) {
            int length = invocation.getArguments().length;
            String[] args = Stream.iterate(0, i -> i + 1).limit(length).map(i -> String.valueOf(i)).toArray(String[]::new);
            invokeMethod.setArgs(args);
        }
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
     * @return 接口调用的信息
     */
    private MethodInterfaceInvoke getMethodInterfaceInvokeFromServlet() {
        if(!HttpServletUtils.isRequest()) {
            return null;
        }
        MethodInterfaceInvoke interfaceInvoke = new MethodInterfaceInvoke();
        interfaceInvoke.setMethod(HttpServletUtils.getMethod());
        Map<String, String> headers = HttpServletUtils.getHeaders();
        interfaceInvoke.setHeaders(headers);
        // 获取用户唯一标识
        SupportHolder instance = SupportHolder.getInstance();
        if (instance.existsBean(IUserId.class)) {
            // 自定义设置
            IUserId bean = instance.getBean(IUserId.class);
            if(AopUtils.isAopProxy(bean)) {
                // 如果实现IUserId接口的类，符合当前切面的表达式，则IUserId使用原对象，而不使用代理对象
                bean = (IUserId)AopProxyUtils.getSingletonTarget(bean);
            }
            interfaceInvoke.setUserId(bean.getUserId());
        }
        interfaceInvoke.setRemoteAddr(HttpServletUtils.remoteAddress());
        interfaceInvoke.setSchema(HttpServletUtils.getSchema());
        interfaceInvoke.setPayload(HttpServletUtils.compositeMap());
        interfaceInvoke.setResponseStatus(HttpServletUtils.getResponseStatus());
        return interfaceInvoke;
    }

    /**
     * 获取方法注解的属性
     * @param method
     * @return 获取方法注解的属性
     */
    private Map<Class, Map<String, Object>> getMethodAnnotation(Method method) {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Annotation[] annotations = AnnotationUtils.getAnnotations(method);
        for(Annotation annotation : annotations) {
            Map<String, Object> annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation);
            map.put(annotation.annotationType(), annotationAttributes);
        }
        return map;
    }

    @Override
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }
}
