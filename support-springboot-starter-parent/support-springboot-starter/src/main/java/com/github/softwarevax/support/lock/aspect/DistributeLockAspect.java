package com.github.softwarevax.support.lock.aspect;

import com.github.softwarevax.support.lock.configuration.DefaultThreadFactory;
import com.github.softwarevax.support.lock.configuration.Lock;
import com.github.softwarevax.support.lock.configuration.LockConstant;
import com.github.softwarevax.support.lock.configuration.enums.LockEnum;
import com.github.softwarevax.support.lock.service.LockService;
import com.github.softwarevax.support.lock.service.impl.DatabaseLockServiceImpl;
import com.github.softwarevax.support.lock.service.impl.RedisLockServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@ConditionalOnProperty(name = "support.lock.enable", havingValue = "true")
public class DistributeLockAspect implements SmartInitializingSingleton, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DistributeLockAspect.class);

    private static final String LOCK_SERVICE_NAME = "lockService";

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new DefaultThreadFactory());

    private ThreadLocal<String> lockKey = new ThreadLocal<>();

    private LockService lockService;

    @Autowired
    private LockConstant constant;

    // order = 1
    @Before(value = "@annotation(com.github.softwarevax.support.lock.configuration.Lock)")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method == null) {
            return;
        }
        Lock lock = method.getAnnotation(Lock.class);
        long timeOut = lock.timeout();
        String key = lock.key();
        String keyName = getLockKey(key, joinPoint);
        lockKey.set(keyName);
        try {
            logger.info("lockKey = {}", keyName);
            Future<Boolean> future = executor.submit(() -> lockService.lock(keyName, timeOut));
            boolean aBoolean = future.get(timeOut, TimeUnit.MILLISECONDS);
            Assert.isTrue(aBoolean, "分布式锁获取失败");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    // order = 2
    @Around(value = "@annotation(com.github.softwarevax.support.lock.configuration.Lock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    // order = 3
    @After(value = "@annotation(com.github.softwarevax.support.lock.configuration.Lock)")
    public void after(JoinPoint joinPoint){
    }

    // order = 4
    @AfterReturning(pointcut = "@annotation(com.github.softwarevax.support.lock.configuration.Lock)",returning = "ret")
    public void doAfterReturning(Object ret) {
        String key = lockKey.get();
        executor.submit(() -> lockService.unLock(key));
        lockKey.remove();
    }

    // order = 方法抛出异常时
    @AfterThrowing(pointcut = "@annotation(com.github.softwarevax.support.lock.configuration.Lock)",throwing = "ex")
    public void AfterThrowing(JoinPoint joinPoint, Throwable ex){
        String key = lockKey.get();
        executor.submit(() -> lockService.unLock(key));
        lockKey.remove();
    }

    private String getLockKey(String key, JoinPoint joinPoint) {
        if(StringUtils.isNotBlank(key)) {
            return key;
        }
        logger.info(key);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] names = StringUtils.split(method.toString(), " ");
        for(String name : names) {
            if(StringUtils.indexOf(name, "(") > -1 && StringUtils.indexOf(name, ")") > -1) {
                return name;
            }
        }
        return null;
    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) applicationContext;
        BeanDefinitionBuilder beanDefinitionBuilder = null;
        LockEnum lockType = constant.getType();
        Assert.notNull(lockType, "请配置分布式锁类型：lock.type");
        logger.info("分布式锁类型 = {}", lockType.name());
        Class<? extends LockService> clazz = null;
        switch (lockType) {
            case Redis: {
                clazz = RedisLockServiceImpl.class;
                beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                RedisTemplate template = (RedisTemplate) ctx.getBean("redisTemplate");
                Assert.notNull(template, "RedisTemplate 未配置");
                Object[] args = new Object[] {template, constant};
                for (Object arg : args) {
                    beanDefinitionBuilder.addConstructorArgValue(arg);
                }
                break;
            }
            case Oracle:
            case MySQL: {
                clazz = DatabaseLockServiceImpl.class;
                beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
                Assert.notNull(template, "JdbcTemplate 未配置");
                Object[] args = new Object[] {template, constant};
                for (Object arg : args) {
                    beanDefinitionBuilder.addConstructorArgValue(arg);
                }
                break;
            }
        }
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) ctx.getBeanFactory();
        beanFactory.registerBeanDefinition(LOCK_SERVICE_NAME, beanDefinition);
        lockService = ctx.getBean(LockService.class);
        logger.info("{} 注册完成", LOCK_SERVICE_NAME);
    }
}
