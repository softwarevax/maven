package com.github.softwarevax.support.application;

/**
 * @author twcao
 * @title: Property
 * @projectName plugin-parent
 * @description: 属性键名
 * @date 2022/6/30 14:20
 */
public interface PropertyKey {

    /**
     * 是否完成初始化
     */
    String INIT_FINISH = "init_finish";

    /**
     * 应用名称
     */
    String APPLICATION_NAME = "application_name";

    /**
     * 启动时间
     */
    String LAUNCH_TIME = "launch_time";

    /**
     * method的配置
     */
    String METHOD_CONSTANT = "method_constant";

    /**
     * support共用的配置
     */
    String SUPPORT_CONSTANT = "support_constant";

    /**
     * lock的配置
     */
    String LOCK_CONSTANT = "lock_constant";

    /**
     * result的配置
     */
    String RESULT_CONSTANT = "result_constant";

    /**
     * pagination的配置
     */
    String PAGINATION_CONSTANT = "pagination_constant";
}
