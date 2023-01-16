package com.example.SchedulingBatchAndQuartz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication


public class SchedulingBatchAndQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingBatchAndQuartzApplication.class, args);
	}

}
