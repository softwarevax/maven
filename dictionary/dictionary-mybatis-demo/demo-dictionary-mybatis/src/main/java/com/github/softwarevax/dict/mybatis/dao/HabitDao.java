package com.github.softwarevax.dict.mybatis.dao;

import com.github.softwarevax.dict.mybatis.domain.Habit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HabitDao {
    List<Habit> queryList();
}
