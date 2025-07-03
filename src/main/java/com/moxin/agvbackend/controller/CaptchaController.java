package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.vo.CaptchaVO;
import com.moxin.agvbackend.service.CaptchaService;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.utils.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public Result getCaptcha() {
        CaptchaVO captcha = captchaService.generate();
        return Result.builder().code(ResultCode.SUCCESS).message("获取成功").data(captcha).build();
    }
}
