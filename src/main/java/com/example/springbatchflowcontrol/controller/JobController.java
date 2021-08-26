package com.example.springbatchflowcontrol.controller;

import lombok.SneakyThrows;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobOperator jobOperator;


    @SneakyThrows
    @GetMapping("/restart/{id}")
    public String restart(@PathVariable long id){
        jobOperator.restart(id);
        return "OK";
    }


}
