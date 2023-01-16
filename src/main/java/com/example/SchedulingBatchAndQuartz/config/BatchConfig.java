package com.example.SchedulingBatchAndQuartz.config;


import com.example.SchedulingBatchAndQuartz.tasks.MyTaskTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import com.example.SchedulingBatchAndQuartz.tasks.MyTaskOne;

import java.sql.Driver;


@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfiguration {

    @Override
    public PlatformTransactionManager getTransactionManager() {
        try {
            return new DataSourceTransactionManager(dataSource());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public PlatformTransactionManager txManager() throws ClassNotFoundException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public DataSource dataSource() throws ClassNotFoundException {
        SimpleDriverDataSource ds=new SimpleDriverDataSource();
        ds.setDriverClass((Class<? extends Driver>) Class.forName("org.h2.Driver"));
        ds.setUrl("jdbc:h2:file:C:/h2TestYeni/demodb");
        ds.setUsername("sa");
        ds.setPassword("123");
        return ds;
    }

    @Bean
    public Step stepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepOne", jobRepository)
                .tasklet((Tasklet) new MyTaskOne(), transactionManager)
                .build();
    }

    @Bean
    public Step stepTwo(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("stepTwo", jobRepository)
                .tasklet((Tasklet) new MyTaskTwo(), transactionManager)
                .build();
    }


    @Bean(name="demoJobOne")
    public Job demoJobOne(JobRepository jobRepository, Step stepOne) {
        return new JobBuilder("demoJobOne", jobRepository)
                .start(stepOne)
                .build();
    }

    @Bean(name="demoJobTwo")
    public Job demoJobTwo(JobRepository jobRepository, Step stepTwo){
        return new JobBuilder("demoJobTwo", jobRepository)
                .start(stepTwo)
                .build();
    }
}

