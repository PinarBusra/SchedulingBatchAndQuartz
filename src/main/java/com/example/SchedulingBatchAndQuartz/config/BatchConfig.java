package com.example.SchedulingBatchAndQuartz.config;


import com.example.SchedulingBatchAndQuartz.model.Employee;
import com.example.SchedulingBatchAndQuartz.tasks.MyTaskTwo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import com.example.SchedulingBatchAndQuartz.tasks.MyTaskOne;

import java.net.MalformedURLException;
import java.nio.file.Path;
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
    public Step stepOne(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws ClassNotFoundException, MalformedURLException {
         return  new StepBuilder("stepOne", jobRepository).<Employee, Employee>chunk(11300,transactionManager).
                reader(reader()).processor(processor()).writer(writer()).build();
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
                .incrementer(new RunIdIncrementer())
                .start(stepOne)
                .build();
    }

    @Bean(name="demoJobTwo")
    public Job demoJobTwo(JobRepository jobRepository, Step stepTwo){
        return new JobBuilder("demoJobTwo", jobRepository)
                .start(stepTwo)
                .build();
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor() {
        return new DBLogProcessor();
    }

    @Bean
    public FlatFileItemReader<Employee> reader() {
        System.out.println("Okuyor.");
        FlatFileItemReader<Employee> itemReader = new FlatFileItemReader<Employee>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("/input.csv"));
        return itemReader;
    }
    @Bean
    public LineMapper<Employee> lineMapper() {
        System.out.println("Mapleme yapıyor.");
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[] { "id", "firstName", "lastName" });
        lineTokenizer.setIncludedFields(new int[] { 0, 1, 2 });
        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<Employee>();
        fieldSetMapper.setTargetType(Employee.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer() throws ClassNotFoundException {
        System.out.println("Insert atıyor.");
        JdbcBatchItemWriter<Employee> itemWriter = new JdbcBatchItemWriter<Employee>();
        itemWriter.setDataSource(dataSource());
        itemWriter.setSql("INSERT INTO EMPLOYEE (ID, FIRSTNAME, LASTNAME) VALUES (:id, :firstName, :lastName)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
        return itemWriter;
    }

}

