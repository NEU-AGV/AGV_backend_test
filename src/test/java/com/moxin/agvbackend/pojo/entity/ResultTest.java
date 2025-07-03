package com.moxin.agvbackend.pojo.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testResultBuilder() {
        // 使用 Builder 模式创建 Result 对象
        Result result = Result.builder()
                .code(200)
                .message("成功")
                .data("测试数据")
                .build();

        // 验证属性设置是否正确
        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMessage());
        assertEquals("测试数据", result.getData());
    }

    @Test
    void testResultAllArgsConstructor() {
        // 使用全参构造函数创建 Result 对象
        Result result = new Result(200, "成功", "测试数据");

        // 验证属性设置是否正确
        assertEquals(200, result.getCode());
        assertEquals("成功", result.getMessage());
        assertEquals("测试数据", result.getData());
    }

    @Test
    void testResultNoArgsConstructor() {
        // 使用无参构造函数创建 Result 对象
        Result result = new Result();

        // 验证初始属性值为 null 或默认值
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getData());
    }
}