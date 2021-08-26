package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CleanAccountTasklet implements Tasklet {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        accountRepository.deleteAll();

        return RepeatStatus.FINISHED;
    }
}
