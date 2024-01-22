package com.github.softwarevax.support.result.aspect;

import com.github.softwarevax.support.result.IResult;
import com.github.softwarevax.support.result.annotation.IgnoreResultWrapper;
import com.github.softwarevax.support.result.configuration.ResultConstant;
import com.github.softwarevax.support.utils.HttpServletUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@ConditionalOnProperty(name = "support.result.enable", havingValue = "true")
public class ResultAspect implements ResponseBodyAdvice<Object> {

    @Autowired
    private ResultConstant constant;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        //判断是否有加自定义注解，有就跳过，不返回返回结果包装实体
        if(!Objects.isNull(constant.getExcludePackages()) && constant.getExcludePackages().size() > 0) {
            String methodName = methodParameter.getMethod().getDeclaringClass().getName();
            List<String> excludePackages = constant.getExcludePackages();
            for(String pkg : excludePackages) {
                if(methodName.startsWith(pkg)) {
                    return false;
                }
            }
        }
        AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
        IgnoreResultWrapper ignoreResultWrapper = AnnotationUtils.findAnnotation(annotatedElement, IgnoreResultWrapper.class);
        return ignoreResultWrapper == null;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 可考虑使用对象池
        IResult result = BeanUtils.instantiateClass(constant.getWrapperImpl());
        if (o instanceof String) {
            // String要特殊处理
            return result.returnString(o);
        } else if(o.getClass() == LinkedHashMap.class && HttpServletUtils.getResponseStatus() >= 500) {
            // 异常时的处理，返回时出错，说明客户端请求正常，不会是4xx
            result.error((LinkedHashMap) o);
        } else if (IResult.class.isAssignableFrom(o.getClass())) {
            //如果是实现IResult的实体，则直接返回
            return o;
        }
        return result.returnDto(o);
    }
}
