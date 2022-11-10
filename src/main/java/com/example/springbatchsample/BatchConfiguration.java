package com.example.springbatchsample;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import(SimpleJobRepository.class)
public class BatchConfiguration {


    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory steps;

    public BatchConfiguration(
                              JobBuilderFactory jobs,
                              StepBuilderFactory steps) {

        this.jobBuilderFactory = jobs;
        this.steps = steps;
    }

    @Bean
    public Step importNews() {
        return steps
            .get("importNews")
            .tasklet(new ImportNewsTasklet())
            .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory
            .get("taskletsJob")
            .start(importNews())
            .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }
}
