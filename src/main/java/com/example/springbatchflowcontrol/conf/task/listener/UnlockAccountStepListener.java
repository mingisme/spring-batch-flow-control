package com.example.springbatchflowcontrol.conf.task.listener;

import com.example.springbatchflowcontrol.conf.task.TransferConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnlockAccountStepListener extends StepExecutionListenerSupport {
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("--Unlock account step listener begin--");
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        int toHandleIndex = executionContext.getInt(TransferConstant.TO_HANDLE_INDEX);
        int total = executionContext.getInt(TransferConstant.TOTAL);
        if(toHandleIndex<total){
            log.info("--return GO_AHEAD--");
            return new ExitStatus(TransferConstant.GO_AHEAD);
        }
        log.info("--Unlock account step listener end--");
        log.info("--return COMPLETED--");
        return  ExitStatus.COMPLETED;
    }
}
