package com.moxin.agvbackend.utils;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CaptchaUtilsTest {

    @Test
    void generateCaptcha_ShouldReturnValidCaptchaObject() throws Exception {
        // 准备测试数据
        String expectedUuid = "123e4567-e89b-12d3-a456-426614174000";
        String expectedCode = "ABCD";
        byte[] expectedImageBytes = new byte[]{1, 2, 3, 4};
        String expectedBase64Prefix = "data:image/png;base64,";

        try (MockedStatic<UUID> mockedUuid = Mockito.mockStatic(UUID.class);
             MockedStatic<CaptchaUtil> mockedCaptchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {

            // 模拟UUID生成
            UUID mockUuid = mock(UUID.class);
            when(mockUuid.toString()).thenReturn(expectedUuid);
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            // 模拟验证码生成
            CircleCaptcha mockCaptcha = mock(CircleCaptcha.class);
            when(mockCaptcha.getCode()).thenReturn(expectedCode);

            // 模拟图片写入输出流
            ByteArrayOutputStream mockOutputStream = new ByteArrayOutputStream() {
                @Override
                public byte[] toByteArray() {
                    return expectedImageBytes;
                }
            };
            doAnswer(invocation -> {
                ByteArrayOutputStream out = invocation.getArgument(0);
                out.write(expectedImageBytes);
                return null;
            }).when(mockCaptcha).write(any(ByteArrayOutputStream.class));

            mockedCaptchaUtil.when(() -> CaptchaUtil.createCircleCaptcha(anyInt(), anyInt(), anyInt(), anyInt()))
                    .thenReturn(mockCaptcha);

            // 执行测试方法
            CaptchaUtils.Captcha result = CaptchaUtils.generateCaptcha();

            // 验证结果
            assertNotNull(result, "Captcha对象不应为null");
            assertEquals(expectedUuid, result.uuid, "UUID不匹配");
            assertEquals(expectedCode, result.code, "验证码不匹配");

            // 验证Base64图片
            assertNotNull(result.imgBase64, "图片Base64不应为null");
            assertTrue(result.imgBase64.startsWith(expectedBase64Prefix), "Base64前缀不正确");

            // 验证Base64内容
            String base64Data = result.imgBase64.substring(expectedBase64Prefix.length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            assertArrayEquals(expectedImageBytes, decodedBytes, "解码后的图片字节不匹配");
        }
    }

    @Test
    void captchaInnerClass_ShouldHaveCorrectStructure() {
        // 测试内部类结构
        CaptchaUtils.Captcha captcha = new CaptchaUtils.Captcha();
        captcha.uuid = "test-uuid";
        captcha.imgBase64 = "test-base64";
        captcha.code = "test-code";

        assertEquals("test-uuid", captcha.uuid, "UUID字段访问不正确");
        assertEquals("test-base64", captcha.imgBase64, "图片Base64字段访问不正确");
        assertEquals("test-code", captcha.code, "验证码字段访问不正确");
    }

    @Test
    void generateCaptcha_ShouldHandleImageWriteError() {
        // 测试图片写入异常情况
        try (MockedStatic<CaptchaUtil> mockedCaptchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {
            // 模拟验证码生成
            CircleCaptcha mockCaptcha = mock(CircleCaptcha.class);
            when(mockCaptcha.getCode()).thenReturn("1234");

            // 模拟写入时抛出异常
            doThrow(new RuntimeException("模拟写入错误")).when(mockCaptcha)
                    .write(any(ByteArrayOutputStream.class));

            mockedCaptchaUtil.when(() -> CaptchaUtil.createCircleCaptcha(anyInt(), anyInt(), anyInt(), anyInt()))
                    .thenReturn(mockCaptcha);

            // 验证是否抛出异常
            assertThrows(RuntimeException.class, CaptchaUtils::generateCaptcha,
                    "应捕获并抛出图片写入异常");
        }
    }
}