package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.dto.EmailDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.service.EmailService;
import com.moxin.agvbackend.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send-email-code")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public Result sendEmail(@RequestBody @Validated EmailDTO email) {
        emailService.sendEmail(email.getEmail());
        return Result.builder().code(ResultCode.SUCCESS).message("邮件发送成功").build();
    }
}
