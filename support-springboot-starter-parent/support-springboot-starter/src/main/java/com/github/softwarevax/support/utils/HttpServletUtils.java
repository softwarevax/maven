package com.github.softwarevax.support.utils;

import com.alibaba.fastjson.JSON;
import com.github.softwarevax.support.method.bean.WebInterface;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.*;

public class HttpServletUtils {


    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    public static String getSessionId() {
        HttpServletRequest request = getRequest();
        return request.getSession().getId();
    }

    public static String getQueryString() {
        HttpServletRequest request = getRequest();
        String queryString = request.getQueryString();
        return queryString;
    }

    public static Map<String, String> getMapFromQueryString() {
        String queryString = getQueryString();
        // 分割多个参数
        List<String> splits = StringUtils.splitToList(queryString, "&");
        // 分割键值对
        return StringUtils.splitToMap(splits, "=");
    }

    public static MediaType getRequestContentType() {
        HttpServletRequest request = getRequest();
        return MediaType.valueOf(request.getContentType());
    }

    public static Map<String, String> getParameter() {
        Map<String, String> result = new HashMap<>();
        HttpServletRequest request = getRequest();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            result.put(parameterName, parameterValue);
        }
        return result;
    }

    public static String remoteAddress() {
        HttpServletRequest request = getRequest();
        return request.getRemoteAddr() + ":" + request.getRemotePort();
    }

    public static String getSchema() {
        HttpServletRequest request = getRequest();
        return request.getScheme();
    }

    public static int getResponseStatus() {
        HttpServletResponse response = getResponse();
        return response.getStatus();
    }

    public static Map<String, String> getHeaders() {
        Map<String, String> result = new HashMap<>();
        HttpServletRequest request = getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            result.put(headerName, headerValue);
        }
        return result;
    }

    public static Map<String, Object> getMapFromBodyJson() {
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            HttpServletRequest request = getRequest();
            BufferedReader br = request.getReader();
            while((str = br.readLine()) != null){
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.length() == 0 ? new HashMap<>() : JSON.parseObject(sb.toString());
    }

    public static Map<String, Object> compositeMap() {
        Map<String, String> mapFromQueryString = getMapFromQueryString();
        Map<String, String> parameter = getParameter();
        Map<String, Object> mapFromBodyJson = getMapFromBodyJson();
        Map<String, Object> map = new HashMap<>();
        map.putAll(mapFromQueryString);
        map.putAll(parameter);
        map.putAll(mapFromBodyJson);
        return map;
    }

    public static String getMethod() {
        HttpServletRequest request = getRequest();
        return request.getMethod();
    }

    /**
     * 获取所有的接口
     * @param ctx
     * @return
     */
    public static Map<String, WebInterface> getAllInterfaces(ApplicationContext ctx) {
        Map<String, WebInterface> map = new HashMap<>();
        String[] beanNamesForType = ctx.getBeanNamesForType(RequestMappingHandlerMapping.class);
        if(ArrayUtils.getLength(beanNamesForType) <= 0) {
            return new HashMap<>();
        }
        RequestMappingHandlerMapping mappings = ctx.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mappings.getHandlerMethods();
        Iterator<Map.Entry<RequestMappingInfo, HandlerMethod>> iterator = handlerMethods.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RequestMappingInfo, HandlerMethod> next = iterator.next();
            RequestMappingInfo key = next.getKey();
            HandlerMethod value = next.getValue();
            String methodName = CommonUtils.getMethodName(value.getMethod());
            map.put(methodName, new WebInterface(key, value));
        }
        return map;
    }
}
