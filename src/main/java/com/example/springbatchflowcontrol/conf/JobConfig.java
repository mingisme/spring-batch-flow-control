package com.example.springbatchflowcontrol.conf;

import com.example.springbatchflowcontrol.conf.task.*;
import com.example.springbatchflowcontrol.conf.task.listener.UnlockAccountStepListener;
import lombok.SneakyThrows;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;

@Configuration
@EnableTask
@EnableBatchProcessing
public class JobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CleanAccountTasklet cleanAccountTasklet;

    @Autowired
    private CreateAccountTasklet initAccountTasklet;

    @Autowired
    private PassTimeTasklet passTimeTasklet;

    @Autowired
    private TransferRequestLoadTasklet transferRequestLoadTasklet;

    @Autowired
    private LockAccountTasklet lockAccountTasklet;

    @Autowired
    private UnlockAccountTasklet unlockAccountTasklet;

    @Autowired
    private TransferTasklet transferTasklet;

    @Autowired
    private UnlockAccountStepListener unlockAccountStepListener;

    @Autowired
    private TransferCheckTasklet transferCheckTasklet;

    @Bean
    public Step transferRequestLoadStep() {
        TaskletStep transferRequestLoadStep = stepBuilderFactory.get("transferRequestLoad")
                .tasklet(transferRequestLoadTasklet).build();
        return transferRequestLoadStep;
    }

    @Bean
    public Step lockAccountStep() {
        TaskletStep lockAccountStep = stepBuilderFactory.get("lockAccount")
                .tasklet(lockAccountTasklet).allowStartIfComplete(true).build();
        return lockAccountStep;
    }

    @Bean
    public Step transferStep(){
        TaskletStep transferStep = stepBuilderFactory.get("transfer")
                .tasklet(transferTasklet).allowStartIfComplete(true).build();
        return transferStep;
    }

    @Bean
    public Step unlockAccountStep() {
        TaskletStep unlockAccountStep = stepBuilderFactory.get("unlockAccount").listener(unlockAccountStepListener)
                .tasklet(unlockAccountTasklet).allowStartIfComplete(true).build();
        return unlockAccountStep;
    }

    @Bean
    public Step transferCheckStep(){
        TaskletStep transferRequestLoadStep = stepBuilderFactory.get("transferCheck")
                .tasklet(transferCheckTasklet).build();
        return transferRequestLoadStep;
    }

    @Bean("transferJob")
    public Job transferJob(){
        Job transferJob = jobBuilderFactory.get("transfer")
                .start(transferRequestLoadStep())
                .next(lockAccountStep())
                .next(transferStep())
                .next(unlockAccountStep())
                .on(TransferConstant.GO_AHEAD).to(lockAccountStep())
                .from(unlockAccountStep()).on(ExitStatus.COMPLETED.getExitCode()).to(transferCheckStep())
                .end().build();
        return transferJob;
    }

    @Bean
    public Step cleanAccountStep() {
        TaskletStep cleanAccountStep = stepBuilderFactory.get("cleanAccount")
                .tasklet(cleanAccountTasklet).build();
        return cleanAccountStep;
    }

    @Bean
    public Step createAccountStep() {
        TaskletStep createAccountStep = stepBuilderFactory.get("createAccount")
                .tasklet(initAccountTasklet).build();
        return createAccountStep;
    }

    @Bean
    public Step passTimeStep() {
        TaskletStep passTimeStep = stepBuilderFactory.get("passTime")
                .tasklet(passTimeTasklet).build();
        return passTimeStep;
    }

    @Bean("resetAccountJob")
    public Job restAccountJob() {
        Job resetAccountJob = jobBuilderFactory.get("resetAccount")
                .start(cleanAccountStep())
                .next(createAccountStep())
                .next(passTimeStep()).build();
        return resetAccountJob;
    }

    @Bean
    public Step helloStep(){
        return stepBuilderFactory.get("helloStep")
                .tasklet((s,c)-> {
                    String id = s.getStepExecution().getJobExecution().getJobParameters().getString("id");
                    s.getStepExecution().getExecutionContext().putString("hello", id);

//                    File file = ResourceUtils.getFile("classpath:sample-2mb-text-file.txt");
//                    String bigStr = FileCopyUtils.copyToString(new FileReader(file));
//                    StringBuilder sb = new StringBuilder();
//                    for(int i=0;i<1;i++) {
//                        sb.append(bigStr);
//                    }
//                    s.getStepExecution().getExecutionContext().putString("plain.text", sb.toString());

                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean("helloJob")
    public Job helloJob(){
        return jobBuilderFactory.get("helloJob")
                .start(helloStep()).build();
    }


    @SneakyThrows
    @Bean("asyncJobLauncher")
    @Primary
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobRegistry jobRegistry, JobLauncher jobLauncher) {

        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);

        return jobOperator;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

}
