package com.github.softwarevax.support.method.configuration;

import com.github.softwarevax.support.method.aspect.MethodInvokeNoticer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

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
     * 切面的顺序, 当一个切点存在于多个切面时，可设置控制顺序
     */
    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * 方法执行完后，将统收集的数据放到此类的callback方法中
     */
    private List<Class<? extends MethodInvokeNoticer>> noticers = new ArrayList<>();

    /**
     * 是否持久化记录（方法，请求参数等数据, 开启后，由PersistenceMethodInvokeNoticer完成数据库入库操作）
     */
    private Boolean persistence = false;

    /**
     * 每次都重置表数据，默认false
     */
    private Boolean resetEveryTime = false;

    /**
     * 用户的标识，取header中的key为"user_id"的值，如果实现了IUserId接口，则IUserId.getUserId的优先级更高
     */
    private String userId = "user_id";

    /**
     * methodTag字段的取值，取methodTag配置的注解的value, 若value为空，则取name,只有开启持久化，此配置才会生效
     * 如配置注解：io.swagger.annotations.ApiOperation，可将注解的value或者name属性存入methodTag字段
     */
    private Class<AnnotatedElement> methodTag;

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

    public List<Class<? extends MethodInvokeNoticer>> getNoticers() {
        return noticers;
    }

    public void setNoticers(List<Class<? extends MethodInvokeNoticer>> noticers) {
        this.noticers = noticers;
    }

    public Boolean getPersistence() {
        return persistence;
    }

    public void setPersistence(Boolean persistence) {
        this.persistence = persistence;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Class<AnnotatedElement> getMethodTag() {
        return methodTag;
    }

    public void setMethodTag(Class<AnnotatedElement> methodTag) {
        this.methodTag = methodTag;
    }

    public Boolean getResetEveryTime() {
        return resetEveryTime;
    }

    public void setResetEveryTime(Boolean resetEveryTime) {
        this.resetEveryTime = resetEveryTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
