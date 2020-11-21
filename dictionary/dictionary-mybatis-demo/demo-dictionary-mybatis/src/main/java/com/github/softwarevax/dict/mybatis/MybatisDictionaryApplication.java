package com.github.softwarevax.dict.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.softwarevax.dict.mybatis.dao")
public class MybatisDictionaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisDictionaryApplication.class, args);
	}

}
