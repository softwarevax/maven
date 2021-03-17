package com.github.softwarevax.web;

import com.github.softwarevax.task.bean.Job;
import com.github.softwarevax.task.configure.SchedulerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class UnifiedTaskScheduleStarterApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(UnifiedTaskScheduleStarterApplication.class, args);
		SchedulerManager ma = ctx.getBean(SchedulerManager.class);

		Job job = ma.addJob(com.github.softwarevax.web.HelloJob.class);

		Thread.sleep(5000);
		ma.shutDown(job.getJobId());
		Thread.sleep(5000);

	}
}
