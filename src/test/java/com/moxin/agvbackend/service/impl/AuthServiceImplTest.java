package com.moxin.agvbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.LoginLogMapper;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.dto.LoginDTO;
import com.moxin.agvbackend.pojo.dto.RegisterDTO;
import com.moxin.agvbackend.pojo.entity.LoginLog;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.utils.BCryptUtil;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private LoginLogMapper loginLogMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String testUsername = "testUser";
    private final String testPassword = "password123";
    private final String testEmail = "test@example.com";
    private final String testUuid = "captcha-123";
    private final String testCaptcha = "ABCD";
    private final String testCode = "123456";
    private final String testIp = "192.168.1.1";
    private final String testUserAgent = "TestBrowser";

    @BeforeEach
    void setUp() {
        // 模拟 Redis 的 ValueOperations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    // 测试登录成功
    @Test
    void login_Success() {
        // 1. 准备测试数据
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword, testCaptcha, testUuid);
        User mockUser = User.builder()
                .id(1L)
                .username(testUsername)
                .password(BCryptUtil.encryptPassword(testPassword)) // 加密密码
                .email("test@example.com") // 添加email，避免null
                .status("live")
                .build();

        // 2. 模拟依赖行为
        when(valueOperations.get("captcha:" + testUuid)).thenReturn(testCaptcha); // 验证码正确
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser); // 用户存在
        when(userMapper.updateById(any(User.class))).thenReturn(1); // 更新登录时间

        // 3. 执行登录
        String token = authService.login(loginDTO, testIp, testUserAgent);

        // 4. 验证结果
        assertNotNull(token);
        verify(redisTemplate.opsForValue()).get("captcha:" + testUuid);
        verify(redisTemplate).delete("captcha:" + testUuid);
        verify(userMapper).updateById(mockUser);
        verify(loginLogMapper).insert(any(LoginLog.class));
        verify(redisTemplate.opsForValue()).set(eq("auth:token:" + token), eq(token), eq(1L), eq(TimeUnit.DAYS));
    }

    // 测试验证码错误
    @Test
    void login_CaptchaInvalid() {
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword, "WRONG", testUuid);
        when(valueOperations.get("captcha:" + testUuid)).thenReturn(testCaptcha); // 返回正确验证码

        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginDTO, testIp, testUserAgent)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("验证码错误或已过期", exception.getMessage());
        verify(redisTemplate, never()).delete("captcha:" + testUuid);
    }

    // 测试验证码过期
    @Test
    void login_CaptchaExpired() {
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword, testCaptcha, testUuid);
        when(valueOperations.get("captcha:" + testUuid)).thenReturn(null); // 验证码不存在

        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginDTO, testIp, testUserAgent)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("验证码错误或已过期", exception.getMessage());
    }

    // 测试用户名不存在
    @Test
    void login_UserNotFound() {
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword, testCaptcha, testUuid);
        when(valueOperations.get("captcha:" + testUuid)).thenReturn(testCaptcha);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // 用户不存在

        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginDTO, testIp, testUserAgent)
        );

        assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    // 测试密码错误
    @Test
    void login_PasswordIncorrect() {
        LoginDTO loginDTO = new LoginDTO(testUsername, "wrongPassword", testCaptcha, testUuid);
        User mockUser = User.builder()
                .username(testUsername)
                .password(BCryptUtil.encryptPassword(testPassword)) // 正确密码的加密值
                .build();

        when(valueOperations.get("captcha:" + testUuid)).thenReturn(testCaptcha);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginDTO, testIp, testUserAgent)
        );

        assertEquals(ResultCode.UNAUTHORIZED, exception.getCode());
        assertEquals("用户名或密码错误", exception.getMessage());
    }

    // 测试账号被禁用
    @Test
    void login_AccountDisabled() {
        LoginDTO loginDTO = new LoginDTO(testUsername, testPassword, testCaptcha, testUuid);
        User mockUser = User.builder()
                .username(testUsername)
                .password(BCryptUtil.encryptPassword(testPassword))
                .status("disabled") // 非活跃状态
                .build();

        when(valueOperations.get("captcha:" + testUuid)).thenReturn(testCaptcha);
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(mockUser);

        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginDTO, testIp, testUserAgent)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("该账号已被禁用", exception.getMessage());
    }

    // 测试注册成功
    @Test
    void register_Success() {
        // 1. 准备测试数据
        RegisterDTO registerDTO = new RegisterDTO(testUsername, testPassword, testEmail, testCode);

        // 2. 模拟依赖行为
        when(valueOperations.get("mail:code:" + testEmail)).thenReturn(testCode); // 验证码正确
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L); // 用户名和邮箱均未注册

        // 3. 执行注册
        authService.register(registerDTO);

        // 4. 验证结果
        verify(userMapper).insert(any(User.class));
        verify(redisTemplate).delete("mail:code:" + testEmail);
    }

    // 测试邮箱验证码错误
    @Test
    void register_EmailCodeInvalid() {
        RegisterDTO registerDTO = new RegisterDTO(testUsername, testPassword, testEmail, "WRONG");
        when(valueOperations.get("mail:code:" + testEmail)).thenReturn(testCode); // 返回正确验证码

        AppException exception = assertThrows(AppException.class, () ->
                authService.register(registerDTO)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("邮箱验证码错误或已过期", exception.getMessage());
        verify(userMapper, never()).insert((User) any());
    }

    // 测试邮箱验证码过期
    @Test
    void register_EmailCodeExpired() {
        RegisterDTO registerDTO = new RegisterDTO(testUsername, testPassword, testEmail, testCode);
        when(valueOperations.get("mail:code:" + testEmail)).thenReturn(null); // 验证码不存在

        AppException exception = assertThrows(AppException.class, () ->
                authService.register(registerDTO)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("邮箱验证码错误或已过期", exception.getMessage());
    }

    // 测试用户名已存在
    @Test
    void register_UsernameExists() {
        RegisterDTO registerDTO = new RegisterDTO(testUsername, testPassword, testEmail, testCode);
        when(valueOperations.get("mail:code:" + testEmail)).thenReturn(testCode);
        // 当查询用户名时返回1（表示存在），而查询邮箱时不需要执行（因为已经因为用户名存在而抛出异常了）
        when(userMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(1L); // 第一次调用（查询用户名）就返回1
        AppException exception = assertThrows(AppException.class, () ->
                authService.register(registerDTO)
        );
        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("用户名已存在", exception.getMessage());
        verify(userMapper, never()).insert((User) any());
    }

    // 测试邮箱已被注册
    @Test
    void register_EmailRegistered() {
        RegisterDTO registerDTO = new RegisterDTO(testUsername, testPassword, testEmail, testCode);
        when(valueOperations.get("mail:code:" + testEmail)).thenReturn(testCode);

        // 第一次查询用户名不存在，第二次查询邮箱已存在
        when(userMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L)  // 用户名不存在
                .thenReturn(1L); // 邮箱已存在

        AppException exception = assertThrows(AppException.class, () ->
                authService.register(registerDTO)
        );

        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("邮箱已被注册", exception.getMessage());
        verify(userMapper, never()).insert((User) any());
    }
}