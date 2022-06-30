package com.github.softwarevax.support.method.aspect;

import com.github.softwarevax.support.method.aspect.advice.AbstractExpressMethodAdvice;

import java.util.List;

public interface MethodAdvisor {

    void register(AbstractExpressMethodAdvice advice);

    default void register(List<AbstractExpressMethodAdvice> advices) {
        for(AbstractExpressMethodAdvice advice : advices) {
            register(advice);
        }
    };
}
