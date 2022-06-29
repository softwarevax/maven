package com.github.softwarevax.support.method.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

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
}
