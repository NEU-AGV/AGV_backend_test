package com.moxin.agvbackend.controller;


import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.service.UserService;
import com.moxin.agvbackend.utils.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result getProfile() {
        User user = userService.getProfile();
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("获取成功")
                .data(user)
                .build();
    }

}
