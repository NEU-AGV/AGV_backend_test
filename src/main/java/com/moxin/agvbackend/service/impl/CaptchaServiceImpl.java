package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.pojo.vo.CaptchaVO;
import com.moxin.agvbackend.service.CaptchaService;
import com.moxin.agvbackend.utils.CaptchaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public CaptchaVO generate() {
        CaptchaUtils.Captcha captcha = CaptchaUtils.generateCaptcha();

        // Redis 存储验证码，3分钟有效
        redisTemplate.opsForValue().set("captcha:" + captcha.uuid, captcha.code, Duration.ofMinutes(3));

        return CaptchaVO.builder()
                .uuid(captcha.uuid)
                .img(captcha.imgBase64)
                .build();
    }
}
