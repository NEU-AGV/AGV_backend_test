package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.service.EmailService;
import com.moxin.agvbackend.utils.EmailUtils;
import com.moxin.agvbackend.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendEmail(String email) {
        Integer code = EmailUtils.genEmailCode();
        String message = EmailUtils.genEmailMessage(code);
        String subject = EmailUtils.genEmailSubject();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        try {
            mailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException(ResultCode.FAIL, "邮件发送失败");
        }

        String redisKey = "mail:code:" + email;
        stringRedisTemplate.opsForValue().set(redisKey, code.toString(), 10, TimeUnit.MINUTES);
    }

}
