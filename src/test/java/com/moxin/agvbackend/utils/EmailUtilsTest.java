package com.moxin.agvbackend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilsTest {

    @Test
    void testGenEmailMessage() {
        Integer code = 123456;
        String message = EmailUtils.genEmailMessage(code);

        assertTrue(message.contains(code.toString()));
    }

    @Test
    void testGenEmailSubject() {
        String subject = EmailUtils.genEmailSubject();
        assertEquals("【仓储管理系统】邮箱验证码", subject);
    }

    @Test
    void testGenEmailCode() {
        Integer code = EmailUtils.genEmailCode();
        assertTrue(code >= 100000 && code <= 999999);
    }
}