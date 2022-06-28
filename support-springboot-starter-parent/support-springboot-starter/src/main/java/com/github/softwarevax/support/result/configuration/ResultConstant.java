package com.github.softwarevax.support.result.configuration;

import com.github.softwarevax.support.result.IResult;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
}
