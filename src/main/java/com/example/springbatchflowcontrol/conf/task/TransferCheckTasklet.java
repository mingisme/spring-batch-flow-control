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
import java.util.List;


@Slf4j
@Component
public class TransferCheckTasklet implements Tasklet {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--Transfer check begin--");
        List<Account> accounts = accountRepository.findAll();
        BigDecimal total = accounts.stream().map(a -> a.getBalance()).reduce(BigDecimal.valueOf(0), (a, b) -> a.add(b));
        log.info("--Actual total {}--", total);
        BigDecimal deviation = total.subtract(BigDecimal.valueOf(1000 * 10)).abs();
        log.info("--Deviation {}--", deviation);
        if (deviation.compareTo(BigDecimal.valueOf(0.01)) > 0) {
            throw new RuntimeException("Deviation is so big: " + deviation);
        }
        log.info("--Transfer check end--");
        return RepeatStatus.FINISHED;
    }
}
