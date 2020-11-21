package com.github.softwarevax.dict.mybatis.controller;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.mybatis
 * @Description:
 * @date 2020/11/21 13:35
 */
import com.github.softwarevax.dict.mybatis.dao.HabitDao;
import com.github.softwarevax.dict.mybatis.domain.Habit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description habitcontroller
 * @project dictionary-plugins-demo
 * @classname HabitController
 * @date 2020/11/19 16:50
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
