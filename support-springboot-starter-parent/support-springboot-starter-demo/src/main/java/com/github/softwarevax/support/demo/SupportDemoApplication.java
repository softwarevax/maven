package com.github.softwarevax.support.demo;

import com.github.softwarevax.support.EnableSupport;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSupport
@MapperScan(value = "com.github.softwarevax.support.demo.mapper")
@SpringBootApplication(scanBasePackages = "com.github.softwarevax.support.demo")
public class SupportDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportDemoApplication.class, args);
    }
}
