package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.ResourceLockRepository;
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

import java.util.List;

@Slf4j
@Component
public class UnlockAccountTasklet implements Tasklet {

    @Autowired
    private ResourceLockRepository resourceLockRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--Unlock account begin--");
        StepExecution stepExecution = contribution.getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();

        int index = jobExecution.getExecutionContext().getInt(TransferConstant.TO_HANDLE_INDEX);
        log.info("--Current index {} --", index);

        ObjectMapper objectMapper = new ObjectMapper();
        String detail = jobExecution.getExecutionContext().getString(TransferConstant.DETAIL);
        List<TransferItem> transferItems = objectMapper.readValue(detail, new TypeReference<List<TransferItem>>() {
        });

        TransferItem transferItem = transferItems.get(index);
        int compare = transferItem.getFrom().compareTo(transferItem.getTo());
        if (compare > 0) {
            unlockAccount(transferItem.getFrom());
            unlockAccount(transferItem.getTo());
        } else {
            unlockAccount(transferItem.getTo());
            unlockAccount(transferItem.getFrom());
        }

        index = index + 1;
        jobExecution.getExecutionContext().putInt(TransferConstant.TO_HANDLE_INDEX, index);
        log.info("--Change index to {} --", index);
        log.info("--Unlock account end--");
        return RepeatStatus.FINISHED;
    }

    private void unlockAccount(String accountId) {
        resourceLockRepository.deleteById(accountId);
    }

}
