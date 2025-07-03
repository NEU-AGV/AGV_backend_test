package com.moxin.agvbackend.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppExceptionTest {

    // 正常情况：测试带 code 和 message 的构造函数
    @Test
    void testConstructorWithCodeAndMessage() {
        int code = 500;
        String message = "业务异常";
        AppException exception = new AppException(code, message);

        assertEquals(code, exception.getCode());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause()); // cause 应为 null
    }

    // 边界情况：测试 message 为 null
    @Test
    void testConstructorWithNullMessage() {
        AppException exception = new AppException(500, null);

        assertEquals(500, exception.getCode());
        assertNull(exception.getMessage());
    }
}