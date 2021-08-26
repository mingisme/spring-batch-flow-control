package com.example.springbatchflowcontrol.service;

import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("resetAccount")
    private Job resetAccountJob;

    @SneakyThrows
    public void resetAccounts() {

        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("jobId", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        jobLauncher.run(resetAccountJob, jobParameters);

    }

}
