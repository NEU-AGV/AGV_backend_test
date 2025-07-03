package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.utils.ResultCode;
import com.moxin.agvbackend.utils.ThreadLocalUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getProfile_UserExists_ReturnsUser() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1001); // Integer类型的ID
        User expectedUser = new User();
        expectedUser.setId(1001L);

        // 模拟静态工具类
        try (MockedStatic<ThreadLocalUtil> mocked = mockStatic(ThreadLocalUtil.class)) {
            mocked.when(ThreadLocalUtil::get).thenReturn(claims);

            // 模拟Mapper行为
            when(userMapper.selectById(1001L)).thenReturn(expectedUser);

            // 执行测试
            User actualUser = userService.getProfile();

            // 验证结果
            assertNotNull(actualUser);
            assertEquals(1001L, actualUser.getId());
            verify(userMapper, times(1)).selectById(1001L);
        }
    }

    @Test
    void getProfile_UserNotExists_ThrowsAppException() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 9999); // 不存在的ID

        // 模拟静态工具类
        try (MockedStatic<ThreadLocalUtil> mocked = mockStatic(ThreadLocalUtil.class)) {
            mocked.when(ThreadLocalUtil::get).thenReturn(claims);

            // 模拟Mapper返回null
            when(userMapper.selectById(9999L)).thenReturn(null);

            // 执行测试并验证异常
            AppException exception = assertThrows(AppException.class,
                    () -> userService.getProfile()
            );

            // 验证异常内容
            assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
            assertEquals("用户不存在", exception.getMessage());
            verify(userMapper, times(1)).selectById(9999L);
        }
    }

    @Test
    void getProfile_ZeroUserId_ThrowsAppException() {
        // 准备边界值测试（ID=0）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 0); // 边界值ID

        try (MockedStatic<ThreadLocalUtil> mocked = mockStatic(ThreadLocalUtil.class)) {
            mocked.when(ThreadLocalUtil::get).thenReturn(claims);

            // 模拟Mapper返回null
            when(userMapper.selectById(0L)).thenReturn(null);

            // 执行测试
            AppException exception = assertThrows(AppException.class,
                    () -> userService.getProfile()
            );

            // 验证异常
            assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
            assertEquals("用户不存在", exception.getMessage());
        }
    }

    @Test
    void getProfile_NegativeUserId_ThrowsAppException() {
        // 准备边界值测试（负ID）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", -100); // 负ID

        try (MockedStatic<ThreadLocalUtil> mocked = mockStatic(ThreadLocalUtil.class)) {
            mocked.when(ThreadLocalUtil::get).thenReturn(claims);

            // 模拟Mapper返回null
            when(userMapper.selectById(-100L)).thenReturn(null);

            // 执行测试
            AppException exception = assertThrows(AppException.class,
                    () -> userService.getProfile()
            );

            // 验证异常
            assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
            assertEquals("用户不存在", exception.getMessage());
        }
    }
}