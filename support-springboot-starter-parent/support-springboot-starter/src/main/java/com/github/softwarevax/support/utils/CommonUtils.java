package com.github.softwarevax.support.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author twcao
 * @title: CommonUtils
 * @projectName plugin-parent
 * @description: 共用工具类
 * @date 2022/6/29 16:19
 */
public class CommonUtils {

    public static String getMethodName(Method method) {
        // 方法所在的类名
        String className = method.getDeclaringClass().getCanonicalName();
        // 方法名
        String methodName = method.getName();
        StringBuilder fullMethodName = new StringBuilder();
        fullMethodName.append(className).append(".").append(methodName);
        Parameter[] parameters = method.getParameters();
        fullMethodName.append("(");
        if(parameters.length > 0) {
            StringBuilder parameterSb = new StringBuilder();
            for (int i = 0, len = parameters.length; i < len; i++) {
                Parameter parameter = parameters[i];
                String parameterType = parameter.getType().getCanonicalName();
                parameterSb.append(parameterType);
                if(i < len - 1) {
                    parameterSb.append(",");
                }
            }
            fullMethodName.append(parameterSb);
        }
        fullMethodName.append(")");
        return fullMethodName.toString();
    }
}
