package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.dto.LoginDTO;
import com.moxin.agvbackend.pojo.dto.RegisterDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.service.AuthService;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    private LoginDTO validLoginDto;
    private RegisterDTO validRegisterDto;

    @BeforeEach
    void setUp() {
        validLoginDto = LoginDTO.builder()
                .username("testuser")
                .password("Test@1234")
                .captcha("1234")
                .uuid("test-uuid")
                .build();

        validRegisterDto = new RegisterDTO();
        validRegisterDto.setUsername("newuser");
        validRegisterDto.setPassword("New@1234");
        validRegisterDto.setEmail("newuser@example.com");
        validRegisterDto.setCode("123456");
    }

    @Test
    void login_Success() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("test-agent");
        when(authService.login(any(LoginDTO.class), anyString(), anyString())).thenReturn("mock-token");

        // Act
        Result result = authController.login(validLoginDto, request);

        // Assert
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("mock-token", ((Map<?, ?>) result.getData()).get("token"));

        verify(authService).login(validLoginDto, "127.0.0.1", "test-agent");
    }

    @Test
    void login_WithXForwardedForHeader() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1");
        when(request.getHeader("User-Agent")).thenReturn("test-agent");
        when(authService.login(any(LoginDTO.class), anyString(), anyString())).thenReturn("mock-token");

        // Act
        Result result = authController.login(validLoginDto, request);

        // Assert
        assertNotNull(result);
        verify(authService).login(validLoginDto, "192.168.1.1", "test-agent");
    }

    @Test
    void login_WithBlankUsername() {
        // Arrange
        LoginDTO invalidDto = LoginDTO.builder()
                .username("")
                .password("Test@1234")
                .captcha("1234")
                .uuid("test-uuid")
                .build();

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "loginDTO");
        bindingResult.rejectValue("username", "NotBlank", "用户名不能为空");

        // Act & Assert
        assertThrows(MethodArgumentNotValidException.class,
                () -> {
                    if (bindingResult.hasErrors()) {
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }
                    authController.login(invalidDto, request);
                });
    }

    @Test
    void login_WithBlankCaptcha() {
        // Arrange
        LoginDTO invalidDto = LoginDTO.builder()
                .username("testuser")
                .password("Test@1234")
                .captcha("")
                .uuid("test-uuid")
                .build();

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "loginDTO");
        bindingResult.rejectValue("captcha", "NotBlank", "验证码不能为空");

        // Act & Assert
        assertThrows(MethodArgumentNotValidException.class,
                () -> {
                    if (bindingResult.hasErrors()) {
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }
                    authController.login(invalidDto, request);
                });
    }

    @Test
    void register_Success() throws Exception {
        // Arrange
        doNothing().when(authService).register(any(RegisterDTO.class));

        // Act
        Result result = authController.register(validRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("注册成功", result.getMessage());
        assertNull(result.getData());

        verify(authService).register(validRegisterDto);
    }

    @Test
    void register_WithBlankUsername() {
        // Arrange
        RegisterDTO invalidDto = new RegisterDTO();
        invalidDto.setUsername("");
        invalidDto.setPassword("New@1234");
        invalidDto.setEmail("user@example.com");
        invalidDto.setCode("123456");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "registerDTO");
        bindingResult.rejectValue("username", "NotBlank", "用户名不能为空");

        // Act & Assert
        assertThrows(MethodArgumentNotValidException.class,
                () -> {
                    if (bindingResult.hasErrors()) {
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }
                    authController.register(invalidDto);
                });
    }

    @Test
    void register_WithInvalidEmail() {
        // Arrange
        RegisterDTO invalidDto = new RegisterDTO();
        invalidDto.setUsername("newuser");
        invalidDto.setPassword("New@1234");
        invalidDto.setEmail("invalid-email");
        invalidDto.setCode("123456");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "registerDTO");
        bindingResult.rejectValue("email", "Email", "邮箱格式不正确");

        // Act & Assert
        assertThrows(MethodArgumentNotValidException.class,
                () -> {
                    if (bindingResult.hasErrors()) {
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }
                    authController.register(invalidDto);
                });
    }

    @Test
    void register_WithBlankCode() {
        // Arrange
        RegisterDTO invalidDto = new RegisterDTO();
        invalidDto.setUsername("newuser");
        invalidDto.setPassword("New@1234");
        invalidDto.setEmail("user@example.com");
        invalidDto.setCode("");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "registerDTO");
        bindingResult.rejectValue("code", "NotBlank", "验证码不能为空");

        // Act & Assert
        assertThrows(MethodArgumentNotValidException.class,
                () -> {
                    if (bindingResult.hasErrors()) {
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }
                    authController.register(invalidDto);
                });
    }

    @Test
    void login_WithEmptyXForwardedForHeader() throws Exception {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("test-agent");
        when(authService.login(any(LoginDTO.class), anyString(), anyString())).thenReturn("mock-token");

        // Act
        Result result = authController.login(validLoginDto, request);

        // Assert
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("mock-token", ((Map<?, ?>) result.getData()).get("token"));

        verify(authService).login(validLoginDto, "127.0.0.1", "test-agent");
    }
}