package com.github.softwarevax.support.demo.mapper;

import com.github.softwarevax.support.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> list();
}
