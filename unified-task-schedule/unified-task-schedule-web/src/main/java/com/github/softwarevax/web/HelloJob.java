package com.github.softwarevax.web;

import com.github.softwarevax.task.CronExpress;

/**
 * @author twcao
 * @description TODO
 * @project unified-task-schedule
 * @classname HelloJob
 * @date 2021/3/16 15:03
 */
@CronExpress("0/1 * * * * *")
public class HelloJob implements Runnable {

    @Override
    public void run(){
        System.out.println("hello msg" + Thread.currentThread().getId());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }
}
