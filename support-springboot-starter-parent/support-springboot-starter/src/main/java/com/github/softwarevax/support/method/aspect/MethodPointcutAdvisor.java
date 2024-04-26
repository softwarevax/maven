package com.github.softwarevax.support.method.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.lang.Nullable;

import java.io.Serializable;

public class MethodPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable {

    private Pointcut pointcut = Pointcut.TRUE;

    public MethodPointcutAdvisor() {
    }

    public MethodPointcutAdvisor(Advice advice) {
        this(Pointcut.TRUE, advice);
    }

    public MethodPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        setAdvice(advice);
    }

    public void setPointcut(@Nullable Pointcut pointcut) {
        this.pointcut = (pointcut != null ? pointcut : Pointcut.TRUE);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": pointcut [" + getPointcut() + "]; advice [" + getAdvice() + "]";
    }

}
