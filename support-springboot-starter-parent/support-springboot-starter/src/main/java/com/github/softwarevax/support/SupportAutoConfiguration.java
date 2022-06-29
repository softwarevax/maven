package com.github.softwarevax.support;

import com.github.softwarevax.support.lock.configuration.LockConstant;
import com.github.softwarevax.support.method.aspect.MethodInterceptorAdvice;
import com.github.softwarevax.support.method.bean.WebInterface;
import com.github.softwarevax.support.method.configuration.MethodConstant;
import com.github.softwarevax.support.page.configuration.PaginationConstant;
import com.github.softwarevax.support.result.configuration.ResultConstant;
import com.github.softwarevax.support.utils.HttpServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import java.util.Map;

@ComponentScan(basePackages = {"com.github.softwarevax.support"})
@EnableConfigurationProperties(value = {LockConstant.class, ResultConstant.class, PaginationConstant.class, MethodConstant.class})
public class SupportAutoConfiguration implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SupportAutoConfiguration.class);

    private ApplicationContext ctx;

    @Autowired
    private MethodConstant methodConstant;

    @Bean
    @ConditionalOnProperty(value = "support.method.enable", havingValue = "true")
    public DefaultPointcutAdvisor methodAdvisor() {
        Assert.hasText(methodConstant.getExpress(), "请配置切点表达式");
        MethodInterceptorAdvice interceptor = new MethodInterceptorAdvice(ctx);
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(methodConstant.getExpress());
        logger.info("method aop expression = {}", methodConstant.getExpress());
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    /**
     * 系统启动完成后，获取所有接口
     * @param applicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Map<String, WebInterface> interfaces = HttpServletUtils.getAllInterfaces(this.ctx);
        DefaultPointcutAdvisor advisor = ctx.getBean(DefaultPointcutAdvisor.class);
        MethodInterceptorAdvice advice = (MethodInterceptorAdvice) advisor.getAdvice();
        advice.setInterfaceMaps(interfaces);
    }
}
