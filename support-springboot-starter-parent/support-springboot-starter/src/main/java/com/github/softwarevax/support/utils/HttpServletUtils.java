package com.github.softwarevax.support.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServletUtils {

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
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
}
