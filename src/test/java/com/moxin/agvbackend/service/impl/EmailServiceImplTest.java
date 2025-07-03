package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private EmailServiceImpl emailService;

    private final String testEmail = "test@example.com";
    private final String fromEmail = "noreply@example.com";

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
    }

    @Test
    void sendEmail_Success() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(testEmail));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(stringRedisTemplate.opsForValue(), times(1))
                .set(anyString(), anyString(), eq(10L), eq(TimeUnit.MINUTES));
    }

    @Test
    void sendEmail_Fail_ThrowsAppException() {
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        Exception exception = assertThrows(Exception.class,
                () -> emailService.sendEmail(testEmail));

        assertTrue(exception instanceof AppException);
        assertEquals(ResultCode.FAIL, ((AppException) exception).getCode());
        assertEquals("邮件发送失败", exception.getMessage());

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(stringRedisTemplate.opsForValue(), never())
                .set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void sendEmail_VerifyMessageContent() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(testEmail);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(fromEmail, sentMessage.getFrom());
        assertEquals(testEmail, sentMessage.getTo()[0]);
        assertNotNull(sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
    }

    @Test
    void sendEmail_VerifyRedisKeyFormat() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(testEmail);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(stringRedisTemplate.opsForValue()).set(keyCaptor.capture(), anyString(), anyLong(), any(TimeUnit.class));

        String redisKey = keyCaptor.getValue();
        assertEquals("mail:code:" + testEmail, redisKey);
    }
}