package com.github.softwarevax.web;

import com.github.softwarevax.task.bean.Job;
import com.github.softwarevax.task.bean.JobHandler;
import com.github.softwarevax.task.configure.SchedulerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
public class UnifiedTaskScheduleStarterApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(UnifiedTaskScheduleStarterApplication.class, args);
		SchedulerManager schedulerManager = ctx.getBean(SchedulerManager.class);
		Job job = schedulerManager.addJob(com.github.softwarevax.web.HelloJob.class);
		JobHandler jobHandler = schedulerManager.getJobHandler(job.getJobId());
		ThreadPoolTaskScheduler scheduler = jobHandler.getScheduler();
		while (true) {
			Thread.sleep(1000);
			System.out.println(scheduler.getActiveCount());
		}
	}
}
