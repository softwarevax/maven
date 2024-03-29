package com.github.softwarevax.dict.starter.mybatis.domain;

import com.github.softwarevax.dict.core.Dict;
import lombok.Data;

import java.util.List;

/**
 * @author ctw
 * 2020/11/22 20:03
 */
@Data
public class Habit {

    /**
     * 主键
     */
    private String id;

    /**
     * 名称
     */
    private String habitName;

    /**
     * 对象: 字典嵌套
     */
    @Dict
    private ExtendUser createUser;

    /**
     * 集合: 字典嵌套
     */
    @Dict
    private List<ExtendUser> createUsers;

    @Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
    private String updateUserId;

    private String updateUserName;

    @Dict
    private String state;
}
