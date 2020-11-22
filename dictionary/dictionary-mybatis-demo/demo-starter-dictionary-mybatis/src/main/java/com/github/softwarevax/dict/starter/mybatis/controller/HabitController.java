package com.github.softwarevax.dict.starter.mybatis.controller;

import com.github.softwarevax.dict.starter.mybatis.dao.HabitDao;
import com.github.softwarevax.dict.starter.mybatis.domain.Habit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 2020/11/19 16:50
 */
@RestController
public class HabitController {

    @Autowired
    HabitDao habitDao;

    @GetMapping("/habit/queryList")
    public List<Habit> list() {
        return habitDao.queryList();
    }
}
