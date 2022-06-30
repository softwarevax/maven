package com.github.softwarevax.support.application;

import com.github.softwarevax.support.SupportAutoConfiguration;
import com.github.softwarevax.support.configure.SupportConstant;
import com.github.softwarevax.support.configure.ThreadPoolDemander;
import com.github.softwarevax.support.method.aspect.MethodInterceptorAdvisor;
import com.github.softwarevax.support.method.aspect.MethodInvokeNoticer;
import com.github.softwarevax.support.method.aspect.PersistenceMethodInvokeNoticer;
import com.github.softwarevax.support.method.aspect.advice.DefaultExpressMethodAdvice;
import com.github.softwarevax.support.method.bean.WebInterface;
import com.github.softwarevax.support.method.configuration.MethodConstant;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SupportHolder {

    private static final Logger logger = LoggerFactory.getLogger(SupportAutoConfiguration.class);

    /**
     * 单例实例
     */
    private static SupportHolder holder;

    /**
     * support需要的线程池
     */
    private ThreadPoolTaskExecutor executor;

    /**
     * spring上下文
     */
    private ApplicationContext springCtx;

    /**
     * support上下文
     */
    private SupportContext supportCtx;

    /**
     * web应用的所有接口，含RequestMappings和HandleMethod
     */
    private Map<String, WebInterface> interfaceMaps;

    public static SupportHolder getInstance() {
        if(Objects.isNull(holder)) {
            holder = new SupportHolder();
        }
        return holder;
    }

    private SupportHolder() {
        supportCtx = new SupportContext();
        supportCtx.put(PropertyKey.INIT_FINISH, false);
    }

    public ThreadPoolTaskExecutor getThreadPoolExecutor() {
        return this.executor;
    }

    public void setApplicationContext(ApplicationContext springCtx) {
        this.springCtx = springCtx;
    }

    public ApplicationContext getApplicationContext() {
        return this.springCtx;
    }

    /**
     * 存入值
     * @param key 键
     * @param obj 值
     */
    public void put(String key, Object obj) {
        supportCtx.put(key, obj);
    }

    /**
     * 获取值，转换失败排除异常
     * @param key 键
     * @param <T> 值
     * @return 值
     */
    public <T> T get(String key) {
        return supportCtx.get(key);
    }

    public Map<String, WebInterface> getInterfaces() {
        return this.interfaceMaps;
    }

    public <T> List<T> getBeans(Class<T> clazz) {
        Map<String, T> beansOfType = springCtx.getBeansOfType(clazz);
        return new ArrayList<>(beansOfType.values());
    }

    public void initializeLoaded() {
        if(supportCtx.get(PropertyKey.INIT_FINISH)) {
            return;
        }
        initCommon();
        initMethod();
        supportCtx.put(PropertyKey.INIT_FINISH, true);
    }

    /**
     * 初始化共用的
     */
    private void initCommon() {
        // 1、线程池
        ThreadPoolTaskExecutor executor = null;
        try {
            executor = springCtx.getBean(ThreadPoolTaskExecutor.class);
        } catch (Exception e) {
            logger.warn("应用未定义线程池，创建默认线程池");
        }
        if(Objects.isNull(executor)) {
            // 如果应用没有初始化线程池，则创建一个(spring有默认的线程池)
            SupportConstant supportConstant = supportCtx.get(PropertyKey.SUPPORT_CONSTANT);
            executor = supportConstant.getThreadPool().threadPoolExecutor();
        }
        this.executor = executor;
        // 为需要使用线程池的切面，设置线程池
        String[] beanNamesForType = springCtx.getBeanNamesForType(ThreadPoolDemander.class);
        for (String beanName : beanNamesForType) {
            ThreadPoolDemander bean = springCtx.getBean(beanName, ThreadPoolDemander.class);
            bean.setThreadPoolTaskExecutor(executor);
        }
        // 2、读取web应用的接口
        this.interfaceMaps = HttpServletUtils.getAllInterfaces(springCtx);
        String applicationName = StringUtils.getFirstNotBlank(StringUtils.strip(springCtx.getApplicationName(), "/"), springCtx.getId());
        supportCtx.put(PropertyKey.APPLICATION_NAME, applicationName);
    }

    /**
     * 初始化method功能
     */
    private void initMethod() {
        // 1、初始化method需要的配置
        DefaultPointcutAdvisor advisor = springCtx.getBean(DefaultPointcutAdvisor.class);
        MethodInterceptorAdvisor advice = (MethodInterceptorAdvisor) advisor.getAdvice();
        DefaultExpressMethodAdvice defaultAdvice = new DefaultExpressMethodAdvice();
        MethodConstant constant = supportCtx.get(PropertyKey.METHOD_CONSTANT);
        defaultAdvice.setExpress(constant.getExpress());
        advice.register(defaultAdvice);
        // 需要被通知的类
        List<Class<? extends MethodInvokeNoticer>> noticerClazz = constant.getNoticers();
        // 需要被通知的对象
        List<MethodInvokeNoticer> noticers = noticerClazz.stream().map(row -> BeanUtils.instantiateClass(row)).collect(Collectors.toList());
        if(constant.getPersistence()) {
            PersistenceMethodInvokeNoticer noticer = new PersistenceMethodInvokeNoticer();
            noticer.setTemplate(springCtx.getBean(JdbcTemplate.class));
            noticers.add(noticer);
        }
        defaultAdvice.addNoticers(noticers);
        defaultAdvice.setThreadPoolTaskExecutor(this.executor);
    }

    /**
     * 动态注册bean，aspect无法被动态注册
     * @param name
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    public <T> T registerBean(String name, Class<T> clazz, Object ... args) {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) this.springCtx;
        if(springCtx.containsBean(name)) {
            Object bean = springCtx.getBean(name);
            if (bean.getClass().isAssignableFrom(clazz)) {
                return (T) bean;
            } else {
                throw new RuntimeException("BeanName 重复 " + name);
            }
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        for (Object arg : args) {
            beanDefinitionBuilder.addConstructorArgValue(arg);
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) ctx.getBeanFactory();
        beanFactory.registerBeanDefinition(name, beanDefinition);
        return springCtx.getBean(name, clazz);
    }
}
