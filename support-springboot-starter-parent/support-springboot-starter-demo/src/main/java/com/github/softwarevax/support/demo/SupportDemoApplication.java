package com.github.softwarevax.support.demo;

import com.github.softwarevax.support.EnableSupport;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@EnableSupport
@MapperScan(value = "com.github.softwarevax.support.demo.mapper")
@SpringBootApplication(scanBasePackages = "com.github.softwarevax.support.demo")
public class SupportDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SupportDemoApplication.class, args);
    }
}
