package com.moxin.agvbackend.interception;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.utils.JwtUtil;
import com.moxin.agvbackend.utils.ResultCode;
import com.moxin.agvbackend.utils.ThreadLocalUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginInterceptorTest {

    private HandlerInterceptor interceptor;
    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        interceptor = new LoginInterceptor();
        stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);

        // 使用反射注入私有成员变量
        Field field = LoginInterceptor.class.getDeclaredField("stringRedisTemplate");
        field.setAccessible(true);
        field.set(interceptor, stringRedisTemplate);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @AfterEach
    void tearDown() {
        ThreadLocalUtil.remove();
    }

    @Test
    void testPreHandleSuccess() throws Exception {
        String token = "testToken";
        when(valueOperations.get("auth:token:" + token)).thenReturn(token);

        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "value");

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.parseToken(token)).thenReturn(claims);

            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            when(request.getHeader("Authorization")).thenReturn(token);

            Object handler = Mockito.mock(HandlerMapping.class);
            boolean result = interceptor.preHandle(request, response, handler);

            assertTrue(result);
            assertEquals(claims, ThreadLocalUtil.get());
        }
    }

    @Test
    void testPreHandleTokenNotFoundInRedis() {
        String token = "testToken";
        when(valueOperations.get("auth:token:" + token)).thenReturn(null);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(request.getHeader("Authorization")).thenReturn(token);

        Object handler = Mockito.mock(HandlerMapping.class);
        AppException exception = assertThrows(AppException.class, () -> {
            interceptor.preHandle(request, response, handler);
        });

        assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
        assertEquals("未登录", exception.getMessage());
    }

    @Test
    void testPreHandleTokenParseException() {
        String token = "testToken";
        when(valueOperations.get("auth:token:" + token)).thenReturn(token);

        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            mockedJwtUtil.when(() -> JwtUtil.parseToken(token)).thenThrow(new RuntimeException());

            HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            when(request.getHeader("Authorization")).thenReturn(token);

            Object handler = Mockito.mock(HandlerMapping.class);
            AppException exception = assertThrows(AppException.class, () -> {
                interceptor.preHandle(request, response, handler);
            });

            assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
            assertEquals("未登录", exception.getMessage());
        }
    }

    @Test
    void testAfterCompletion() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "value");
        ThreadLocalUtil.set(claims);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object handler = Mockito.mock(HandlerMapping.class);
        interceptor.afterCompletion(request, response, handler, null);

        assertNull(ThreadLocalUtil.get());
    }
}