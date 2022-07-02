package com.github.softwarevax.support.demo.service.impl;

import com.github.softwarevax.support.demo.entity.User;
import com.github.softwarevax.support.demo.mapper.UserMapper;
import com.github.softwarevax.support.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author twcao
 * @title: UserServiceimpl
 * @projectName plugin-parent
 * @description: TODO
 * @date 2022/6/29 11:22
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMapper userMapper;


    @Override
    public List<User> list() {
        return userMapper.list();
    }
}
