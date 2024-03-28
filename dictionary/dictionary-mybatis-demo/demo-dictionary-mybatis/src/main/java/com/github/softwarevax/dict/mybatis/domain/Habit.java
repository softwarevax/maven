package com.github.softwarevax.dict.mybatis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.softwarevax.dict.core.Dict;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class Habit {

    private String id;
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

    /**
     * 非字典，用户表，
     */
    @Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
    private String updateUserId;

    private String updateUserName;

    @Dict
    private String state;
}
