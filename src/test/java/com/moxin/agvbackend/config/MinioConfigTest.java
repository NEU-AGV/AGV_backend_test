package com.moxin.agvbackend.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

// 启用配置属性
@EnableConfigurationProperties(MinioConfig.class)
// 启动 Spring Boot 测试环境
@SpringBootTest(classes = MinioConfig.class)
public class MinioConfigTest {

    @Autowired
    private MinioConfig minioConfig;

    @Test
    public void testMinioClientCreation() {
        // 正常情况：测试 MinioClient 是否能正常创建
        MinioClient client = minioConfig.minioClient();
        assertNotNull(client, "MinioClient 应该被成功创建");
    }

    @Test
    public void testUrlGetterAndSetter() {
        // 正常情况：测试 url 属性的 getter 和 setter 方法
        String testUrl = "http://test-url:9000";
        minioConfig.setUrl(testUrl);
        assertEquals(testUrl, minioConfig.getUrl(), "url 属性的 getter 和 setter 方法应该正常工作");
    }

    @Test
    public void testAccessKeyGetterAndSetter() {
        // 正常情况：测试 accessKey 属性的 getter 和 setter 方法
        String testAccessKey = "test-access-key";
        minioConfig.setAccessKey(testAccessKey);
        assertEquals(testAccessKey, minioConfig.getAccessKey(), "accessKey 属性的 getter 和 setter 方法应该正常工作");
    }

    @Test
    public void testSecretKeyGetterAndSetter() {
        // 正常情况：测试 secretKey 属性的 getter 和 setter 方法
        String testSecretKey = "test-secret-key";
        minioConfig.setSecretKey(testSecretKey);
        assertEquals(testSecretKey, minioConfig.getSecretKey(), "secretKey 属性的 getter 和 setter 方法应该正常工作");
    }

    @Test
    public void testBucketNameGetterAndSetter() {
        // 正常情况：测试 bucketName 属性的 getter 和 setter 方法
        String testBucketName = "test-bucket-name";
        minioConfig.setBucketName(testBucketName);
        assertEquals(testBucketName, minioConfig.getBucketName(), "bucketName 属性的 getter 和 setter 方法应该正常工作");
    }

    @Test
    public void testNoArgsConstructor() {
        // 正常情况：测试无参构造函数
        MinioConfig config = new MinioConfig();
        assertNotNull(config, "无参构造函数应该能正常创建对象");
    }

    @Test
    public void testAllArgsConstructor() {
        // 正常情况：测试全参构造函数
        String url = "http://test-url:9000";
        String accessKey = "test-access-key";
        String secretKey = "test-secret-key";
        String bucketName = "test-bucket-name";
        MinioConfig config = new MinioConfig(url, accessKey, secretKey, bucketName);
        assertEquals(url, config.getUrl(), "全参构造函数应该正确设置 url 属性");
        assertEquals(accessKey, config.getAccessKey(), "全参构造函数应该正确设置 accessKey 属性");
        assertEquals(secretKey, config.getSecretKey(), "全参构造函数应该正确设置 secretKey 属性");
        assertEquals(bucketName, config.getBucketName(), "全参构造函数应该正确设置 bucketName 属性");
    }
}