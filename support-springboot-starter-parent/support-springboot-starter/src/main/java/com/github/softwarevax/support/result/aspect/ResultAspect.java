package com.github.softwarevax.support.result.aspect;

import com.alibaba.fastjson.JSON;
import com.github.softwarevax.support.result.IResult;
import com.github.softwarevax.support.result.annotation.IgnoreResultWrapper;
import com.github.softwarevax.support.result.configuration.ResultConstant;
import com.github.softwarevax.support.utils.HttpServletUtils;
import com.github.softwarevax.support.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
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
        String methodName = methodParameter.getMethod().getDeclaringClass().getName();
        if(StringUtils.isNotBlank(constant.getBasePackages()) && !StringUtils.startsWith(methodName, constant.getBasePackages())) {
            return false;
        }
        //判断是否有加自定义注解，有就跳过，不返回返回结果包装实体
        if(!Objects.isNull(constant.getExcludePackages()) && constant.getExcludePackages().size() > 0) {
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

    /**
     * 如果设置全局异常处理，则会先经过全局异常处理，然后将处理后的结果传到此处
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 可考虑使用对象池
        IResult result = BeanUtils.instantiateClass(constant.getWrapperImpl());
        // 响应的状态码
        int code = HttpServletUtils.getResponseStatus();
        if(code == HttpStatus.OK.value()) {
            if(o instanceof String) {
                // String要特殊处理
                return result.toJSONString(true, code, o, null);
            } else if (IResult.class.isAssignableFrom(o.getClass())) {
                //如果是实现IResult的实体，则直接返回
                return o;
            }
            return result.returnDTO(true, code, o, null);
        } else {
            if(o instanceof LinkedHashMap) {
                LinkedHashMap map = (LinkedHashMap)o;
                return result.returnDTO(false, code, null, (String) map.get("message"));
            }
        }
        return o;
    }

    /**
     * 请求参数为空(必传的参数没传)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public IResult paramMissingException(MissingServletRequestParameterException e) {
        IResult result = BeanUtils.instantiateClass(constant.getWrapperImpl());
        String msg = String.format("请求参数：%s不能为空！", e.getParameterName());
        return result.returnDTO(false, 200, null, msg);
    }

    /**
     * 数据库唯一键冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public IResult paramMissingException(DuplicateKeyException e) {
        IResult result = BeanUtils.instantiateClass(constant.getWrapperImpl());
        String msg = String.format("请求参数：%s不能为空！", String.format("数据库唯一键冲突[%s]", e.getCause().getLocalizedMessage()));
        return result.returnDTO(false, 200, null, msg);
    }

    /**
     * 参数校验异常(@Validated和@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public IResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        IResult result = BeanUtils.instantiateClass(constant.getWrapperImpl());
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errors = new ArrayList<>();
        for(ObjectError oe : allErrors) {
            errors.add(oe.getObjectName() + "." + ((FieldError) oe).getField() + ":" + oe.getDefaultMessage());
        }
        return result.returnDTO(false, 200, null, JSON.toJSONString(errors));
    }
}
