package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.ResourceLockRepository;
import com.example.springbatchflowcontrol.db.model.ResourceLock;
import com.example.springbatchflowcontrol.db.model.TransferItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class LockAccountTasklet implements Tasklet {

    @Autowired
    private ResourceLockRepository resourceLockRepository;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("--Lock account begin--");
        StepExecution stepExecution = contribution.getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();

        int index = jobExecution.getExecutionContext().getInt(TransferConstant.TO_HANDLE_INDEX);
        log.info("--Current index {} --", index);
        String handledContent = (String) stepExecution.getExecutionContext().get(TransferConstant.LOCK_ACCOUNT_STEP_HANDLED);
        log.info("--Handled indies {} --", handledContent);
        ObjectMapper objectMapper = new ObjectMapper();

        Set<Integer> handled = handledContent != null ? objectMapper.readValue(handledContent, new TypeReference<Set<Integer>>() {
        }) : new HashSet<>();

        if (handled == null || !handled.contains(index)) {
            log.info("--Handle current index {} --", index);

            JobParameters jobParameters = jobExecution.getJobParameters();
            String id = jobParameters.getString(TransferConstant.REQUEST_ID);

            String detail = jobExecution.getExecutionContext().getString(TransferConstant.DETAIL);
            List<TransferItem> transferItems = objectMapper.readValue(detail, new TypeReference<List<TransferItem>>() {
            });

            TransferItem transferItem = transferItems.get(index);
            int compare = transferItem.getFrom().compareTo(transferItem.getTo());
            if (compare < 0) {
                lockAccount(transferItem.getFrom(), id);
                lockAccount(transferItem.getTo(), id);
            } else {
                lockAccount(transferItem.getTo(), id);
                lockAccount(transferItem.getFrom(), id);
            }

            handled.add(index);
            stepExecution.getExecutionContext().putString(TransferConstant.LOCK_ACCOUNT_STEP_HANDLED, objectMapper.writeValueAsString(handled));
            log.info("--Lock account end--");
        } else {
            log.info("--Noop current index {} --", index);
        }
        log.info("--Lock account end--");
        return RepeatStatus.FINISHED;
    }

    private void lockAccount(String accountId, String requestId) {
        int count = resourceLockRepository.countByResourceIdAndCorrelatedId(accountId, requestId);
        if (count == 0) {
            ResourceLock resourceLock = new ResourceLock(accountId, requestId, "T", new Date());
            resourceLockRepository.save(resourceLock);
        }
    }

}
