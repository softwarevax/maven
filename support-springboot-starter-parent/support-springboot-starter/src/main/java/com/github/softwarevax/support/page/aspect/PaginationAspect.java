package com.github.softwarevax.support.page.aspect;

import com.github.pagehelper.PageHelper;
import com.github.softwarevax.support.page.OrderByInterceptor;
import com.github.softwarevax.support.page.Pagination;
import com.github.softwarevax.support.page.configuration.PaginationConstant;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@ConditionalOnProperty(value = "support.page.enable", havingValue = "true")
public class PaginationAspect implements SmartInitializingSingleton {

    /**
     * 日志
     */
    public static Logger logger = LoggerFactory.getLogger(PaginationAspect.class);

    @Autowired
    private PaginationConstant constant;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 定义切入点
     * 切点不能包含当切面方法，否则造成无限循环
     */
    @Pointcut("@annotation(com.github.softwarevax.support.page.Pagination)")
    public void pagination(){}

    /**
     * 前置通知：在连接点之前执行的通知
     * @param point
     * @throws Throwable
     */
    @Before("pagination()")
    public void doBefore(JoinPoint point) {
        if(!checkRequestValid()) {
            return;
        }
        // 1、获取方法上的注解值和默认的配置值
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Pagination annotation = method.getAnnotation(Pagination.class);
        // pageSize参数名
        String pageSize = pageValue(annotation.pageSize(), constant.getPageSize());
        // pageNum参数名
        String pageNum = pageValue(annotation.pageNum(), constant.getPageNum());
        // orderBy参数名
        String orderBy = pageValue(annotation.orderBy(), constant.getOrderBy());
        // 用户定义的最大页大小
        int userDefineMaxPageSize = annotation.maxPageSize();
        // 2、参数校验pageSize、pageNum、maxPageSize
        Map<String, Object> parameterMap = HttpServletUtils.compositeMap();
        if(!parameterMap.containsKey(pageSize)) {
            logger.warn("{}参数名为空，分页不生效", pageSize);
            return;
        }
        if(!parameterMap.containsKey(pageNum)) {
            logger.warn("{}参数名为空，分页不生效", pageNum);
            return;
        }
        Integer pageSizeVal = parseInt(parameterMap.get(pageSize));
        Integer pageNumVal = parseInt(parameterMap.get(pageNum));
        if(Objects.isNull(pageSizeVal) || pageSizeVal <= 0) {
            logger.warn("{}参数值为无效，分页不生效", pageSize);
            return;
        }
        if(Objects.isNull(pageNumVal) || pageNumVal <= 0) {
            logger.warn("{}参数值为无效，分页不生效", pageNum);
            return;
        }
        // 3、maxPageSize，优先使用注解Pagination中的值，如果没有设置，则使用配置中的值，若没有配置，则不校验
        Integer maxPageSize = constant.getMaxPageSize();
        if(userDefineMaxPageSize > 0) {
            maxPageSize = userDefineMaxPageSize;
        }
        if(maxPageSize != null && maxPageSize > 0) {
            Assert.isTrue(pageSizeVal <= maxPageSize, "分页大小不得大于" + maxPageSize);
        }
        if(parameterMap.containsKey(orderBy)) {
            // 有排序，则设置排序
            String order = String.valueOf(parameterMap.get(orderBy));
            OrderByInterceptor.setOrderBy(order);
        }
        // 分页
        PageHelper.startPage(pageNumVal, pageSizeVal);

    }

    @AfterReturning(returning = "ret", pointcut = "pagination()")
    public void doAfterReturning(Object ret) {
        if(constant.getClearPage()) {
            // 用完之后清理分页
            PageHelper.clearPage();
        }
    }

    @AfterThrowing(value = "pagination()", throwing = "e")
    public void myAfterThrowing(Throwable e){
        if(constant.getClearPage()) {
            // 若抛出异常，清理分页
            PageHelper.clearPage();
        }
    }

    /**
     * post请求，只支持content-type = MediaType.APPLICATION_FORM_URLENCODED || content-type = MediaType.APPLICATION_JSON
     * @return
     */
    private boolean checkRequestValid() {
        String method = HttpServletUtils.getMethod();
        if("POST".equals(method)) {
            MediaType contentType = HttpServletUtils.getRequestContentType();
            if(!contentType.equals(MediaType.APPLICATION_FORM_URLENCODED) && !contentType.equals(MediaType.APPLICATION_JSON)) {
                return false;
            }
        }
        return true;
    }

    private String pageValue(String userDefined, String defaultValue) {
        if(StringUtils.isBlank(userDefined)) {
            return defaultValue;
        }
        return userDefined;
    }

    private Integer parseInt(Object obj) {
        if(obj == null) {
            return null;
        }
        String str = String.valueOf(obj);
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 添加排序插件
        sqlSessionFactory.getConfiguration().addInterceptor(new OrderByInterceptor());
    }
}
