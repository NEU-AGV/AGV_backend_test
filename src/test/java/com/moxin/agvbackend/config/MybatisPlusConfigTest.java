package com.moxin.agvbackend.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MybatisPlusConfigTest {

    @Autowired
    private MybatisPlusConfig mybatisPlusConfig;

    @Test
    public void testMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = mybatisPlusConfig.mybatisPlusInterceptor();
        assertNotNull(interceptor);
        assertEquals(1, interceptor.getInterceptors().size());
        assertTrue(interceptor.getInterceptors().get(0) instanceof PaginationInnerInterceptor);
    }
}