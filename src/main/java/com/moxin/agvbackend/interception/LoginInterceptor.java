package com.moxin.agvbackend.interception;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.utils.JwtUtil;
import com.moxin.agvbackend.utils.ResultCode;
import com.moxin.agvbackend.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String token = request.getHeader("Authorization");
        try {


            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get("auth:token:" + token);

            if (redisToken == null) {
                throw new RuntimeException();
            }

            Map<String, Object> claims = JwtUtil.parseToken(redisToken);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
            throw new AppException(ResultCode.UNAUTHORIZED, "未登录");
        }

    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }

}