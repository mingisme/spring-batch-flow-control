package com.example.springbatchflowcontrol.conf.task;

import com.example.springbatchflowcontrol.db.TransferRequestRepository;
import com.example.springbatchflowcontrol.db.model.TransferItem;
import com.example.springbatchflowcontrol.db.model.TransferRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransferRequestLoadTasklet implements Tasklet {

    @Autowired
    private TransferRequestRepository transferRequestRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info("--Load transfer request begin--");
        JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
        JobParameters jobParameters = jobExecution.getJobParameters();
        String id = jobParameters.getString(TransferConstant.REQUEST_ID);
        log.info("--Transfer request id is {}", id);
        TransferRequest transferRequest = transferRequestRepository.getById(id);
        String detail = transferRequest.getDetail();
        log.info("--Transfer request detail is\n {}", detail);
        jobExecution.getExecutionContext().putString(TransferConstant.DETAIL, detail);

        ObjectMapper objectMapper = new ObjectMapper();
        List<TransferItem> transferItems = objectMapper.readValue(detail, new TypeReference<List<TransferItem>>() {
        });
        jobExecution.getExecutionContext().putInt(TransferConstant.TOTAL, transferItems.size());

        jobExecution.getExecutionContext().putInt(TransferConstant.TO_HANDLE_INDEX, 0);

        log.info("--Load transfer request end--");
        return RepeatStatus.FINISHED;
    }
}
