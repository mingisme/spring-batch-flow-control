package com.example.springbatchflowcontrol.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloBatchController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("helloJob")
    private Job helloJob;

    @GetMapping("/{name}")
    public String hello(@PathVariable String name) throws Exception {
        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("id", new JobParameter(name + " at " + new Date()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        jobLauncher.run(helloJob, jobParameters);
        return "OK";
    }
}
