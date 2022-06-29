package com.github.softwarevax.support.method.configuration;

import com.github.softwarevax.support.method.aspect.MethodListener;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author twcao
 * @title: MethodConstant
 * @projectName plugin-parent
 * @description: 方法配置的常量
 * @date 2022/6/29 10:48
 */
@ConfigurationProperties(prefix = "support.method")
public class MethodConstant {

    /**
     * 是否启用方法切面
     */
    private Boolean enable = false;

    /**
     * 切点表达式,
     * eg: execution(* com.github.softwarevax.support.demo..*.*(..))
     * 第一个星号：返回类型不限制
     * 第二个星号：匹配所有类
     * 第三个星号：匹配所有方法
     * 第一个..: 表示包com.github.softwarevax.support.demo及其子包
     * 第二个..: 表示方法参数不限制
     */
    private String express;

    /**
     * 方法执行完后，将统收集的数据放到此类的callback方法中
     */
    private List<Class<? extends MethodListener>> methodListener;

    /**
     * 是否持久化记录（方法，请求参数等数据）
     */
    private Boolean persistence = false;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public List<Class<? extends MethodListener>> getMethodListener() {
        return methodListener;
    }

    public void setMethodListener(List<Class<? extends MethodListener>> methodListener) {
        this.methodListener = methodListener;
    }

    public Boolean getPersistence() {
        return persistence;
    }

    public void setPersistence(Boolean persistence) {
        this.persistence = persistence;
    }
}
