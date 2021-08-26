package com.example.springbatchflowcontrol.service;

import com.example.springbatchflowcontrol.conf.task.TransferConstant;
import com.example.springbatchflowcontrol.db.TransferRequestRepository;
import com.example.springbatchflowcontrol.db.model.TransferItem;
import com.example.springbatchflowcontrol.db.model.TransferRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("resetAccountJob")
    private Job resetAccountJob;

    @Autowired
    @Qualifier("transferJob")
    private Job transferJob;

    @Autowired
    private TransferRequestRepository transferRequestRepository;

    @SneakyThrows
    public void resetAccounts() {

        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("jobId", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        jobLauncher.run(resetAccountJob, jobParameters);

    }

    @SneakyThrows
    public void randomTransfer() {

        List<TransferItem> transfers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int from = new Random().nextInt(9) + 1;
            int to = from;
            for (int j = 0; j < 100; j++) {
                to = new Random().nextInt(9) + 1;
                if (from != to) {
                    break;
                }
            }
            if (from == to) {
                continue;
            }
            double amount = new Random().nextDouble() * 100;
            transfers.add(new TransferItem("acc" + from, "acc" + to, new BigDecimal(amount)));
        }

        if (transfers.size() == 0) {
            log.error("Transfer items should be more than 0");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String id = UUID.randomUUID().toString();

        String detail = objectMapper.writeValueAsString(transfers);
        log.info("id: {}, detail: {}", id, detail);
        TransferRequest transferRequest = new TransferRequest(id, detail);
        transferRequestRepository.save(transferRequest);

        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put(TransferConstant.REQUEST_ID, new JobParameter(id));
        JobParameters jobParameters = new JobParameters(parameterMap);
        jobLauncher.run(transferJob, jobParameters);

    }

    public void concurrentTransfer() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(this::randomTransfer);
        }
    }
}
