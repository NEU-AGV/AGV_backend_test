package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.dto.EmailDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.service.EmailService;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void sendEmail_WithValidEmail_ShouldReturnSuccess() {
        // 准备测试数据
        EmailDTO validEmail = new EmailDTO();
        validEmail.setEmail("test@example.com");

        // 执行测试
        Result result = emailController.sendEmail(validEmail);

        // 验证结果
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("邮件发送成功", result.getMessage());
        verify(emailService, times(1)).sendEmail("test@example.com");
    }

    @Test
    void sendEmail_WhenEmailServiceFails_ShouldThrowException() {
        // 准备测试数据
        EmailDTO validEmail = new EmailDTO();
        validEmail.setEmail("test@example.com");

        // 模拟服务层抛出异常
        doThrow(new RuntimeException("邮件服务异常"))
                .when(emailService).sendEmail("test@example.com");

        // 执行并验证异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailController.sendEmail(validEmail);
        });

        assertEquals("邮件服务异常", exception.getMessage());
        verify(emailService, times(1)).sendEmail("test@example.com");
    }
}