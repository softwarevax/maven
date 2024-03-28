package com.github.softwarevax.dict.mybatis.domain;

import com.github.softwarevax.dict.core.Dict;
import lombok.Data;

@Data
public class User {
    private String id;
    private String name;

    /**
     * 状态
     * 解释：1、table = sys_config，字典所在的表
     *      2、property = stateLabel，把字典的值，放到同类的stateLabel属性中。若不配置，回放到当前属性，若值的类型跟属性的类型不一致，会默认转为字符串，支持类型转换
     *      3、column = label, 字典的中文
     *      4、value = value，字典的编码
     *      5、conditions，如果value的值是唯一的，则不用conditions来确定唯一。如果不是唯一的，则需根据conditions确定唯一性，否则无法保证字典的映射关系
     */
    @Dict(table = "sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = user_state"})
    private String state;

    private String stateLabel;

    @Dict
    private String sex;
}
