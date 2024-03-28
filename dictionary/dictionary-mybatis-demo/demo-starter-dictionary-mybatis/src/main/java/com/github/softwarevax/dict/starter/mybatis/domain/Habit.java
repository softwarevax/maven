package com.github.softwarevax.dict.starter.mybatis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.softwarevax.dict.core.Dict;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String createUserId;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
    private String updateUserId;

    private String updateUserName;

    @Dict
    private String state;
}
