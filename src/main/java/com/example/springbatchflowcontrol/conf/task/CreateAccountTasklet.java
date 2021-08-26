package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.AccountRepository;
import com.example.springbatchflowcontrol.db.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CreateAccountTasklet implements Tasklet {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        for (int i = 1; i < 11; i++) {
            Account account = new Account();
            account.setAccountNumber("acc" + i);
            account.setBalance(BigDecimal.valueOf(1000));
            accountRepository.save(account);
        }
        return RepeatStatus.FINISHED;
    }
}
