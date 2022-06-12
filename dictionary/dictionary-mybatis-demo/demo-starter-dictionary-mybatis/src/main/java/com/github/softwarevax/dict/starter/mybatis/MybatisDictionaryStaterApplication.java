package com.github.softwarevax.dict.starter.mybatis;

import com.github.softwarevax.dict.mybatis.starter.configuation.annoation.EnableDictionary;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDictionary
@SpringBootApplication
@MapperScan("com.github.softwarevax.dict.mybatis.dao")
public class MybatisDictionaryStaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisDictionaryStaterApplication.class, args);
	}
}
