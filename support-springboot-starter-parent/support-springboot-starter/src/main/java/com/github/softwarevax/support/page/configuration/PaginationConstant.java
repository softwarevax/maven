package com.github.softwarevax.support.page.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "support.page")
public class PaginationConstant {

    /**
     * 是否启用注解分页
     */
    private Boolean enable = false;

    /**
     * pageSize的参数名，页大小(从url，application/x-www-form-urlencoded表单中获取, json第一层中获取，后面考虑支持通过spel表达式获取)，取不到值时，分页不会生效
     */
    private String pageSize = "pageSize";

    /**
     * pageNum，页码(从url，application/x-www-form-urlencoded表单中获取, json第一层中获取，后面考虑支持通过spel表达式获取)，取不到值时，分页不会生效
     */
    private String pageNum = "pageNum";

    /**
     * 排序orderBy
     * 返回前端的实体:
     * User {
     *     String id;
     *     String userName;
     *     String sex;
     * }
     * 排序时
     * eg: userName asc, sex desc
     */
    private String orderBy = "orderBy";

    /**
     * 最大的页大小（含配置值），pageSize过大，查询多时容易导致查询慢, 不宜设置过大, 不设置时，不限制
     * maxPageSize，优先使用注解Pagination中的值，如果没有设置，则使用配置中的值，若没有配置，则不校验
     */
    private Integer maxPageSize;

    /**
     * 方法执行完，是否清除分页信息
     */
    private Boolean clearPage = true;

    /**
     * 如果没有拿到分页相关参数，是否跳过分页，全局默认不跳过，且不可配置，可通过注解Pagination.skipIfMissing单独设置
     * 跳过：即使没拿到，也不会报错，但分页不起效果，此时可当作列表查询使用
     * 不跳过：没拿到参数时，抛出异常
     */
    private Boolean skipIfMissing = false;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(Integer maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    public Boolean getClearPage() {
        return clearPage;
    }

    public void setClearPage(Boolean clearPage) {
        this.clearPage = clearPage;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getSkipIfMissing() {
        return skipIfMissing;
    }
}
