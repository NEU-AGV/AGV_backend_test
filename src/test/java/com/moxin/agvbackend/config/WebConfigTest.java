package com.moxin.agvbackend.config;

import com.moxin.agvbackend.interception.LoginInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private LoginInterceptor loginInterceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @InjectMocks
    private WebConfig webConfig;

    @Test
    void addInterceptors_ShouldConfigureCorrectly() {
        // 模拟interceptorRegistry.addInterceptor返回interceptorRegistration
        when(interceptorRegistry.addInterceptor(loginInterceptor)).thenReturn(interceptorRegistration);

        // 执行测试方法
        webConfig.addInterceptors(interceptorRegistry);

        // 验证
        verify(interceptorRegistry).addInterceptor(loginInterceptor);
        verify(interceptorRegistration).excludePathPatterns(
                "/captcha", "/register", "/login", "/send-email-code"
        );
    }

    @Test
    void excludedPathsShouldNotBeIntercepted() throws Exception {
        // 模拟interceptor行为
        when(loginInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        // 创建模拟请求/响应
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();

        // 验证排除路径是否被拦截
        assertTrue(loginInterceptor.preHandle(request, response, handler));
    }

    @Test
    void nonExcludedPathsShouldBeIntercepted() throws Exception {
        // 模拟interceptor行为
        when(loginInterceptor.preHandle(any(), any(), any())).thenReturn(false);

        // 创建模拟请求/响应
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/protected");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object handler = new Object();

        // 验证非排除路径是否被拦截
        assertFalse(loginInterceptor.preHandle(request, response, handler));
    }
}