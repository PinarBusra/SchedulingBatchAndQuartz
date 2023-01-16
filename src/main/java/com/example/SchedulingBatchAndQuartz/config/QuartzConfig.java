package com.example.SchedulingBatchAndQuartz.config;
import com.example.SchedulingBatchAndQuartz.jobs.CustomQuartzJob;
import lombok.Data;
import org.quartz.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Properties;

@Configuration
@EnableBatchProcessing
@PropertySource(value = "classpath:application.properties")
@Data
@Component
public class QuartzConfig  {
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    @Value("${cronTask.cronTaskOne}")
    private String cronTaskOne;
    @Value("${cronTask.cronTaskTwo}")
    private String cronTaskTwo;

    @Autowired
     public QuartzConfig(JobLauncher jobLauncher,JobLocator jobLocator)
    {
        this.jobLauncher=jobLauncher;
        this.jobLocator=jobLocator;
    }



    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }





    @Bean
   public JobDetail jobOneDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "demoJobOne");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(CustomQuartzJob.class)
                .withIdentity("demoJobOne")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    @Bean
    public JobDetail jobTwoDetail() {
        //Set Job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("jobName", "demoJobTwo");
        jobDataMap.put("jobLauncher", jobLauncher);
        jobDataMap.put("jobLocator", jobLocator);

        return JobBuilder.newJob(CustomQuartzJob.class)
                .withIdentity("demoJobTwo")
                .setJobData(jobDataMap)
                .storeDurably()
                .build();
    }



    @Bean
    public Trigger jobOneTrigger()
    {
       /* SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(10)
                .repeatForever();*/


        return  TriggerBuilder
                .newTrigger()
                .forJob(jobOneDetail())
                .withIdentity("jobOneTrigger")
                //.withSchedule(scheduleBuilder)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronTaskOne))
                .startNow()
                .build();



    }

    @Bean
    public Trigger jobTwoTrigger()
    {
       /* SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(20)
                .repeatForever();*/

        return  TriggerBuilder
                .newTrigger()
                .forJob(jobTwoDetail())
                .withIdentity("jobTwoTrigger")
                //.withSchedule(scheduleBuilder)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronTaskTwo))
                .startNow()
                .build();
    }



    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException
    {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(jobOneTrigger(),jobTwoTrigger());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setJobDetails(jobOneDetail(), jobTwoDetail());
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


}

