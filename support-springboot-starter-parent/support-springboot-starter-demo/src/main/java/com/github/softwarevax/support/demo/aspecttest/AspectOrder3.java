package com.github.softwarevax.support.demo.aspecttest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author twcao
 * @title: AspectOrder3
 * @projectName plugin-parent
 * @description: TODO
 * @date 2022/6/30 15:33
 */
//@Component
@Aspect
@Order(3)
public class AspectOrder3 {
    // 定义切点
    @Pointcut("execution(* com.github.softwarevax.support.demo..*.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void befor() {
        System.out.println("AspectOrder3...befor...");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("AspectOrder3...around...");
        return proceedingJoinPoint.proceed();
    }

    @After("pointCut()")
    public void after() {
        System.out.println("AspectOrder3...after...");
    }

    @AfterReturning("pointCut()")
    public void afterReturning() {
        System.out.println("AspectOrder3...afterReturning...");
    }

}
