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
        /*new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ThreadPoolTaskExecutor executor = ctx.getBean(ThreadPoolTaskExecutor.class);
                ThreadPoolExecutor threadPoolExecutor = executor.getThreadPoolExecutor();
                System.out.println("active count = " + executor.getActiveCount());
                System.out.println("queue size = " + threadPoolExecutor.getQueue().size());
                System.out.println("complete task = " + threadPoolExecutor.getCompletedTaskCount());
                System.out.println("task count = "  + threadPoolExecutor.getTaskCount());
            }
        }).start();*/
    }
}
