package com.moxin.agvbackend.service.impl;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.utils.ResultCode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {
    @Mock
    private MinioClient minioClient;
    @Mock
    private MultipartFile mockFile;
    @InjectMocks
    private UploadServiceImpl uploadService;
    private final String testMinioUrl = "http://minio.example.com";
    private final String testBucketName = "test-bucket";
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(uploadService, "minioUrl", testMinioUrl);
        ReflectionTestUtils.setField(uploadService, "bucketName", testBucketName);
    }
    @Test
    void upload_Success() throws Exception {
        // 模拟文件行为
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        when(mockFile.getInputStream()).thenReturn(mock(InputStream.class));
        when(mockFile.getSize()).thenReturn(1024L);
        // 调用测试方法
        String resultUrl = uploadService.upload(mockFile);
        // 验证MinioClient调用
        ArgumentCaptor<PutObjectArgs> argsCaptor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(argsCaptor.capture());

        // 验证参数传递
        PutObjectArgs capturedArgs = argsCaptor.getValue();
        assertEquals(testBucketName, capturedArgs.bucket());
        assertTrue(capturedArgs.object().endsWith(".png"));
        assertEquals(1024L, capturedArgs.objectSize());
        // 验证返回URL格式
        assertTrue(resultUrl.startsWith(testMinioUrl + "/" + testBucketName + "/"));
        assertTrue(resultUrl.endsWith(".png"));
    }
    @Test
    void upload_WhenMinioThrowsException_ThrowsAppException() throws Exception {
        // 模拟文件行为
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenReturn(mock(InputStream.class));
        when(mockFile.getSize()).thenReturn(2048L);
        // 模拟Minio异常
        doThrow(new RuntimeException("Minio error"))
                .when(minioClient).putObject(any(PutObjectArgs.class));
        // 验证异常抛出
        AppException exception = assertThrows(AppException.class,
                () -> uploadService.upload(mockFile));

        // 验证异常内容
        assertEquals(ResultCode.FAIL, exception.getCode());
        assertEquals("上传失败", exception.getMessage());
    }
    @Test
    void upload_FileNameWithoutExtension() {
        // 测试无扩展名文件
        when(mockFile.getOriginalFilename()).thenReturn("noextension");
        // 验证抛出StringIndexOutOfBoundsException
        assertThrows(StringIndexOutOfBoundsException.class, () -> uploadService.upload(mockFile));
    }
}