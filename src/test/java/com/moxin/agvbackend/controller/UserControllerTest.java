package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.service.UserService;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProfile_Success() {
        // 准备测试数据
        User mockUser = User.builder()
                .id(1L)
                .username("testUser")
                .realName("Test User")
                .departmentId(1L)
                .phone("1234567890")
                .email("test@example.com")
                .role("ADMIN")
                .avatarUrl("http://example.com/avatar.jpg")
                .status("ACTIVE")
                .loginTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 模拟userService的行为
        when(userService.getProfile()).thenReturn(mockUser);

        // 调用被测试方法
        Result result = userController.getProfile();

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());

        User returnedUser = (User) result.getData();
        assertNotNull(returnedUser);
        assertEquals(mockUser.getId(), returnedUser.getId());
        assertEquals(mockUser.getUsername(), returnedUser.getUsername());
        assertEquals(mockUser.getRealName(), returnedUser.getRealName());
        assertEquals(mockUser.getDepartmentId(), returnedUser.getDepartmentId());
        assertEquals(mockUser.getPhone(), returnedUser.getPhone());
        assertEquals(mockUser.getEmail(), returnedUser.getEmail());
        assertEquals(mockUser.getRole(), returnedUser.getRole());
        assertEquals(mockUser.getAvatarUrl(), returnedUser.getAvatarUrl());
        assertEquals(mockUser.getStatus(), returnedUser.getStatus());

        // 验证userService.getProfile()被调用了一次
        verify(userService, times(1)).getProfile();
    }

    @Test
    void getProfile_UserNotFound() {
        // 模拟userService返回null
        when(userService.getProfile()).thenReturn(null);

        // 调用被测试方法
        Result result = userController.getProfile();

        // 验证结果
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());
        assertNull(result.getData());

        // 验证userService.getProfile()被调用了一次
        verify(userService, times(1)).getProfile();
    }

    @Test
    void getProfile_ServiceThrowsException() {
        // 模拟userService抛出异常
        when(userService.getProfile()).thenThrow(new RuntimeException("Database error"));

        // 调用被测试方法并验证异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userController.getProfile();
        });

        assertEquals("Database error", exception.getMessage());

        // 验证userService.getProfile()被调用了一次
        verify(userService, times(1)).getProfile();
    }
}