package com.github.softwarevax.support.method;

public interface MethodSQL {

    /**
     * 字段最大长度
     */
    int FIELD_MAX_LENGTH = 4000;

    /**
     * 检查表是否存在
     */
    String CHECK_METHOD_SQL = "select 1 from t_method where 1 = 0";

    String CHECK_METHOD_INTERFACE_SQL = "select 1 from t_method_interface where 1 = 0";

    String CHECK_METHOD_INVOKE_SQL = "select 1 from t_method_invoke where 1 = 0";

    String CHECK_METHOD_INTERFACE_INVOKE_SQL = "select 1 from t_method_interface_invoke where 1 = 0";

    /**
     * 清空表
     */
    String TRUNCATE_METHOD_SQL = "truncate table t_method";

    String TRUNCATE_METHOD_INTERFACE_SQL = "truncate table t_method_interface";

    String TRUNCATE_METHOD_INVOKE_SQL = "truncate table t_method_invoke";

    String TRUNCATE_METHOD_INTERFACE_INVOKE_SQL = "truncate table t_method_interface_invoke";

    /**
     * 新增方法
     */
    String INSERT_METHOD_SQL = "insert into t_method(application, launch_time, expose, method, method_tag, full_method_name, return_type, parameter) values(?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 新增方法接口
     */
    String INSERT_METHOD_INTERFACE_SQL = "insert into t_method_interface(method_id, method, mappings) values(?, ?, ?)";

    /**
     * 新增方法调用
     */
    String INSERT_METHOD_INVOKE_SQL = "insert into t_method_invoke(session_id, invoke_id, method_id, expose, parameter_val, return_val, start_time, elapsed_time) values(?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 新增方法接口调用
     */
    String INSERT_METHOD_INTERFACE_INVOKE_SQL = "insert into t_method_interface_invoke(invoke_id, scheme, method, remote_addr, headers, payload, response_status, response_body) values(?, ?, ?, ?, ?, ?, ?, ?)";
}
