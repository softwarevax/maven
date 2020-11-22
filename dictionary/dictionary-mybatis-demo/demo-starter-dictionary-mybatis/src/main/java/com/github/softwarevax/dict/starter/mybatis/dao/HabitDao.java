package com.github.softwarevax.dict.starter.mybatis.dao;

import com.github.softwarevax.dict.starter.mybatis.domain.Habit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HabitDao {
    List<Habit> queryList();
}
