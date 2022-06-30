package com.github.softwarevax.support.method.aspect.advice;

import com.github.softwarevax.support.method.aspect.MethodInvokeNoticer;
import org.aopalliance.intercept.MethodInvocation;

import java.util.List;

public interface AbstractExpressMethodAdvice {

    void before(MethodInvocation invocation);

    void afterReturn(Object obj, MethodInvocation invocation);

    void after(MethodInvocation invocation);

    void throwException(MethodInvocation invocation, Throwable e);

    String getExpress();

    void setExpress(String express);

    void addNoticer(MethodInvokeNoticer noticer);

    default void addNoticers(List<MethodInvokeNoticer> noticers) {
        for (MethodInvokeNoticer noticer : noticers) {
            addNoticer(noticer);
        }
    }
}
