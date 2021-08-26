package com.example.springbatchflowcontrol.controller;

import com.example.springbatchflowcontrol.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/reset")
    public String resetAccount(){
        accountService.resetAccounts();
        return "OK";
    }

}
