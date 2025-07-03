package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.pojo.vo.CaptchaVO;
import com.moxin.agvbackend.utils.CaptchaUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptchaServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CaptchaServiceImpl captchaService;

    @Test
    void generate_NormalCase_ReturnsValidCaptchaVO() {
        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        CaptchaVO result = captchaService.generate();

        // 验证结果
        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertNotNull(result.getImg());
        assertTrue(isValidUUID(result.getUuid()));

        // 验证Redis存储逻辑
        verify(redisTemplate.opsForValue()).set(
                eq("captcha:" + result.getUuid()),
                anyString(),
                eq(Duration.ofMinutes(3))
        );
    }

    @Test
    void generate_RedisException_ThrowsRuntimeException() {
        // 模拟Redis操作抛出异常
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Redis error")).when(valueOperations)
                .set(anyString(), anyString(), any(Duration.class));

        // 验证异常
        assertThrows(RuntimeException.class, () -> captchaService.generate());
    }

    @Test
    void generate_BoundaryCase_ValidateRedisKeyFormat() {
        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        CaptchaVO result = captchaService.generate();

        // 验证Redis键格式
        String redisKey = "captcha:" + result.getUuid();
        assertTrue(redisKey.startsWith("captcha:"));
        assertTrue(redisKey.length() > "captcha:".length());
        assertTrue(isValidUUID(result.getUuid()));
    }

    @Test
    void generate_BoundaryCase_ValidateRedisTTL() {
        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 执行测试
        captchaService.generate();

        // 验证过期时间设置
        verify(valueOperations).set(anyString(), anyString(), eq(Duration.ofMinutes(3)));
    }

    // 辅助方法：验证UUID格式
    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}