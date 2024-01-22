package com.github.softwarevax.support.result.configuration;

import com.github.softwarevax.support.result.IResult;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "support.result")
public class ResultConstant {

    /**
     * 是否启用结果包装 仅当support.result.enable=true是开启
     */
    private Boolean enable = false;

    /**
     * 自定义返回结果包装类，默认使用ResultDto
     */
    private Class<? extends IResult> wrapperImpl = com.github.softwarevax.support.result.ResultDto.class;

    /**
     * 不需要对接口的结果进行包装的包名，多个之间，逗号分割
     */
    private List<String> excludePackages;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Class<? extends IResult> getWrapperImpl() {
        return wrapperImpl;
    }

    public void setWrapperImpl(Class<? extends IResult> wrapperImpl) {
        this.wrapperImpl = wrapperImpl;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }
}
