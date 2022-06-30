package com.github.softwarevax.support.method.aspect;

import com.github.softwarevax.support.method.aspect.advice.AbstractExpressMethodAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;

public class MethodInterceptorAdvisor implements MethodInterceptor, MethodAdvisor {

    private AbstractExpressMethodAdvice advice;

    public MethodInterceptorAdvisor() {
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if(Objects.isNull(advice)) {
            return invocation.proceed();
        }
        try {
            advice.before(invocation);
            Object ret = invocation.proceed();
            advice.afterReturn(ret, invocation);
            return ret;
        } catch (Exception e) {
            advice.throwException(invocation, e);
            throw e;
        } finally {
            advice.after(invocation);
        }
    }

    @Override
    public void register(AbstractExpressMethodAdvice advice) {
        this.advice = advice;
    }
}
