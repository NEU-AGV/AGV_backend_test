package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.service.UploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UploadController uploadController;

    @Test
    void upload_Success() throws Exception {
        // 准备测试数据
        String expectedUrl = "http://example.com/file.jpg";
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        // 模拟服务层行为
        when(uploadService.upload(mockFile)).thenReturn(expectedUrl);

        // 调用被测试方法
        Result result = uploadController.upload(mockFile);

        // 验证结果
        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMessage());
        assertEquals(expectedUrl, result.getData());

        // 验证服务层方法被调用
        verify(uploadService, times(1)).upload(mockFile);
    }

    @Test
    void upload_WithEmptyFile() {
        // 准备空文件
        MultipartFile emptyFile = new MockMultipartFile(
                "emptyFile",
                new byte[0]
        );

        // 模拟服务层行为
        when(uploadService.upload(emptyFile)).thenReturn("");

        // 调用被测试方法
        Result result = uploadController.upload(emptyFile);

        // 验证结果
        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMessage());
        assertEquals("", result.getData());
    }

    @Test
    void upload_WithNullFile() {
        // 调用被测试方法
        Result result = uploadController.upload(null);

        // 验证结果
        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void upload_ServiceThrowsException() {
        // 准备测试数据
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        // 模拟服务层抛出异常
        when(uploadService.upload(mockFile)).thenThrow(new RuntimeException("Upload failed"));

        // 调用被测试方法并验证异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            uploadController.upload(mockFile);
        });

        assertEquals("Upload failed", exception.getMessage());
    }
}