package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.dto.LoginDTO;
import com.moxin.agvbackend.pojo.dto.RegisterDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.service.AuthService;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginDTO dto, HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        String ua = request.getHeader("User-Agent");
        String token = authService.login(dto, ip, ua);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("登录成功")
                .data(Map.of("token", token))
                .build();
    }

    @PostMapping("/register")
    public Result register(@RequestBody @Validated RegisterDTO dto) {
        authService.register(dto);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("注册成功")
                .build();
    }
}
