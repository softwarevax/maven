package com.github.softwarevax.dict.starter.mybatis.domain;

import com.github.softwarevax.dict.core.Dict;
import lombok.Data;

/**
 * @author ctw
 * 2020/11/22 20:04
 */
@Data
public class User {

    /**
     * 主键
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 状态
     */
    @Dict(table = "sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = user_state"})
    private String state;

    private String stateLabel;

    @Dict
    private String sex;
}
