package com.moxin.agvbackend.exception;

import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.utils.ResultCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // 正常情况：测试处理 AppException
    @Test
    void testHandleAppException() {
        int code = 403;
        String message = "权限不足";
        AppException exception = new AppException(code, message);

        Result result = handler.handleException(exception);

        // 验证异常类型
        assertTrue(exception instanceof AppException);
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }

    // 正常情况：测试处理普通 Exception
    @Test
    void testHandleGeneralException() {
        String message = "系统错误";
        Exception exception = new Exception(message);

        Result result = handler.handleException(exception);

        assertEquals(ResultCode.FAIL, result.getCode());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
    }

    // 边界情况：测试处理 message 为 null 的 Exception
    @Test
    void testHandleExceptionWithNullMessage() {
        Exception exception = new Exception((String) null);

        Result result = handler.handleException(exception);

        assertEquals(ResultCode.FAIL, result.getCode());
        assertEquals("操作失败", result.getMessage()); // 默认消息
        assertNull(result.getData());
    }

    // 边界情况：测试处理 message 为空字符串的 Exception
    @Test
    void testHandleExceptionWithEmptyMessage() {
        Exception exception = new Exception("");

        Result result = handler.handleException(exception);

        assertEquals(ResultCode.FAIL, result.getCode());
        assertEquals("操作失败", result.getMessage()); // 默认消息
        assertNull(result.getData());
    }

    // 异常情况：测试处理 RuntimeException
    @Test
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("运行时错误");

        Result result = handler.handleException(exception);

        assertEquals(ResultCode.FAIL, result.getCode());
        assertEquals("运行时错误", result.getMessage());
        assertNull(result.getData());
    }

    // 异常情况：测试处理自定义 RuntimeException 子类
    @Test
    void testHandleCustomRuntimeException() {
        class CustomRuntimeException extends RuntimeException {
            public CustomRuntimeException(String message) {
                super(message);
            }
        }

        CustomRuntimeException exception = new CustomRuntimeException("自定义运行时错误");

        Result result = handler.handleException(exception);

        assertEquals(ResultCode.FAIL, result.getCode());
        assertEquals("自定义运行时错误", result.getMessage());
        assertNull(result.getData());
    }
}