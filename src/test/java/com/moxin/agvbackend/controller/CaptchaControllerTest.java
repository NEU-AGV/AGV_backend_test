package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.vo.CaptchaVO;
import com.moxin.agvbackend.service.CaptchaService;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptchaControllerTest {

    @Mock
    private CaptchaService captchaService;

    @InjectMocks
    private CaptchaController captchaController;

    @Test
    void getCaptcha_ShouldReturnSuccessResultWithCaptchaData() {
        // Arrange
        CaptchaVO mockCaptcha = CaptchaVO.builder()
                .uuid("test-uuid")
                .img("test-image-data")
                .build();

        when(captchaService.generate()).thenReturn(mockCaptcha);

        // Act
        Result result = captchaController.getCaptcha();

        // Assert
        assertNotNull(result);
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("获取成功", result.getMessage());

        CaptchaVO returnedCaptcha = (CaptchaVO) result.getData();
        assertNotNull(returnedCaptcha);
        assertEquals("test-uuid", returnedCaptcha.getUuid());
        assertEquals("test-image-data", returnedCaptcha.getImg());

        verify(captchaService, times(1)).generate();
    }

    @Test
    void getCaptcha_ShouldCallServiceExactlyOnce() {
        // Arrange
        when(captchaService.generate()).thenReturn(
                CaptchaVO.builder().build()
        );

        // Act
        captchaController.getCaptcha();

        // Assert
        verify(captchaService, times(1)).generate();
        verifyNoMoreInteractions(captchaService);
    }

    @Test
    void getCaptcha_ShouldReturnNonNullResult() {
        // Arrange
        when(captchaService.generate()).thenReturn(
                CaptchaVO.builder().build()
        );

        // Act
        Result result = captchaController.getCaptcha();

        // Assert
        assertNotNull(result);
    }

    @Test
    void getCaptcha_ShouldReturnResultWithCorrectStructure() {
        // Arrange
        when(captchaService.generate()).thenReturn(
                CaptchaVO.builder().build()
        );

        // Act
        Result result = captchaController.getCaptcha();

        // Assert
        assertAll(
                () -> assertNotNull(result.getCode()),
                () -> assertNotNull(result.getMessage()),
                () -> assertNotNull(result.getData())
        );
    }
}