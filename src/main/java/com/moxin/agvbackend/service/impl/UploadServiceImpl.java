package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.service.UploadService;
import com.moxin.agvbackend.utils.ResultCode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Autowired
    private MinioClient minioClient;

    @Override
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new AppException(ResultCode.FAIL, "上传失败");
        }

        return minioUrl + "/" + bucketName + "/" + filename;
    }
}
