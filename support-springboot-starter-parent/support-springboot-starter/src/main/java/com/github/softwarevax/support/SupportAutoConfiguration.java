package com.github.softwarevax.support;

import com.github.softwarevax.support.application.PropertyKey;
import com.github.softwarevax.support.application.SupportHolder;
import com.github.softwarevax.support.configure.SupportConstant;
import com.github.softwarevax.support.lock.configuration.LockConstant;
import com.github.softwarevax.support.method.aspect.MethodInterceptorAdvisor;
import com.github.softwarevax.support.method.aspect.MethodPointcutAdvisor;
import com.github.softwarevax.support.method.configuration.MethodConstant;
import com.github.softwarevax.support.method.filter.RepeatableRequestBodyFilter;
import com.github.softwarevax.support.page.configuration.PaginationConstant;
import com.github.softwarevax.support.result.configuration.ResultConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

@ComponentScan(basePackages = {"com.github.softwarevax.support"})
@EnableConfigurationProperties(value = {SupportConstant.class, LockConstant.class, ResultConstant.class, PaginationConstant.class, MethodConstant.class})
public class SupportAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SupportAutoConfiguration.class);

    @Autowired
    private SupportConstant supportConstant;

    @Autowired
    private LockConstant lockConstant;

    @Autowired
    private ResultConstant resultConstant;

    @Autowired
    private PaginationConstant paginationConstant;

    @Autowired
    private MethodConstant methodConstant;

    @Bean
    @ConditionalOnProperty(value = "support.method.enable", havingValue = "true")
    public MethodPointcutAdvisor methodAdvisor() {
        Assert.hasText(methodConstant.getExpress(), "请配置切点表达式");
        MethodInterceptorAdvisor interceptor = new MethodInterceptorAdvisor();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(methodConstant.getExpress());
        logger.info("method aop expression = {}", methodConstant.getExpress());
        MethodPointcutAdvisor advisor = new MethodPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        advisor.setOrder(methodConstant.getOrder());
        return advisor;
    }

    @Bean
    @ConditionalOnProperty(value = "support.method.enable", havingValue = "true")
    public FilterRegistrationBean<RepeatableRequestBodyFilter> Filters() {
        FilterRegistrationBean<RepeatableRequestBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RepeatableRequestBodyFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("readRequestBodyFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * 系统启动完成后，获取所有接口
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 存入spring上下文和各功能点的配置
        SupportHolder holder = SupportHolder.getInstance();
        holder.setApplicationContext(event.getApplicationContext());
        holder.put(PropertyKey.METHOD_CONSTANT, methodConstant);
        holder.put(PropertyKey.SUPPORT_CONSTANT, supportConstant);
        holder.put(PropertyKey.LOCK_CONSTANT, lockConstant);
        holder.put(PropertyKey.RESULT_CONSTANT, resultConstant);
        holder.put(PropertyKey.PAGINATION_CONSTANT, paginationConstant);
        holder.initializeLoaded();
        logger.info("[support]加载完成");
    }
}
