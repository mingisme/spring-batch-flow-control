package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.AccountRepository;
import com.example.springbatchflowcontrol.db.model.Account;
import com.example.springbatchflowcontrol.db.model.TransferItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class TransferTasklet implements Tasklet {

    @Autowired
    public AccountRepository accountRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--Transfer begin--");
        StepExecution stepExecution = contribution.getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();

        int index = jobExecution.getExecutionContext().getInt(TransferConstant.TO_HANDLE_INDEX);
        log.info("--Current index {} --", index);

        ObjectMapper objectMapper = new ObjectMapper();

        String detail = jobExecution.getExecutionContext().getString(TransferConstant.DETAIL);
        List<TransferItem> transferItems = objectMapper.readValue(detail, new TypeReference<List<TransferItem>>() {
        });

        TransferItem transferItem = transferItems.get(index);

        String from = transferItem.getFrom();
        String to = transferItem.getTo();
        BigDecimal amount = transferItem.getAmount();

        Optional<Account> fromAccount = accountRepository.findById(from);
        if (fromAccount.get() != null) {
            Account account1 = fromAccount.get();
            BigDecimal balance = account1.getBalance();
            stepExecution.getExecutionContext().putString("transfer.footprint", "Source: " + from + ", Target: " + to + ", Amount: " + amount + ", Balance of source: " + balance);
            if (balance.compareTo(amount) > 0) {
                log.info("--Do transfer index {}, from {}, to {}, amount {} --", index, from, to, amount);
                accountRepository.subtractBalance(from, amount);
                accountRepository.subtractBalance(to, amount.multiply(BigDecimal.valueOf(-1)));
            } else {
                log.info("--Balance is not enough, from {}, balance {}, amount {}", from, balance, amount);
            }
        }

        log.info("--Transfer end--");
        return RepeatStatus.FINISHED;
    }
}
