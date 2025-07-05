package com.moxin.agvbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    @Test
    void genToken_ShouldGenerateValidToken() {
        // 准备测试数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        claims.put("username", "testuser");

        try (MockedStatic<JWT> mockedJWT = Mockito.mockStatic(JWT.class)) {
            // 模拟JWT.create()
            com.auth0.jwt.JWTCreator.Builder builder = mock(com.auth0.jwt.JWTCreator.Builder.class);
            mockedJWT.when(JWT::create).thenReturn(builder);

            // 模拟方法链
            when(builder.withClaim(eq("claims"), any(Map.class))).thenReturn(builder);
            when(builder.withExpiresAt(any(Date.class))).thenReturn(builder);

            // 模拟签名结果
            String expectedToken = "mocked.token.value";
            when(builder.sign(any(Algorithm.class))).thenReturn(expectedToken);

            // 执行测试
            String token = JwtUtil.genToken(claims);

            // 验证结果
            assertEquals(expectedToken, token);
            verify(builder).withClaim("claims", claims);
            verify(builder).withExpiresAt(any(Date.class));
            verify(builder).sign(any(Algorithm.class));
        }
    }



    @Test
    void parseToken_ShouldThrowExceptionForInvalidToken() {
        // 准备测试数据
        String invalidToken = "invalid.token.here";

        try (MockedStatic<JWT> mockedJWT = Mockito.mockStatic(JWT.class)) {
            // 模拟JWT验证流程抛出异常
            com.auth0.jwt.JWTVerifier.BaseVerification verification =
                    mock(com.auth0.jwt.JWTVerifier.BaseVerification.class);
            JWTVerifier verifier = mock(JWTVerifier.class);

            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(verification);
            when(verification.build()).thenReturn(verifier);
            when(verifier.verify(invalidToken)).thenThrow(new JWTVerificationException("Invalid token"));

            // 执行测试并验证异常
            assertThrows(JWTVerificationException.class, () -> JwtUtil.parseToken(invalidToken));
        }
    }

    @Test
    void parseToken_ShouldThrowExceptionForExpiredToken() {
        // 准备测试数据
        String expiredToken = "expired.token.here";

        try (MockedStatic<JWT> mockedJWT = Mockito.mockStatic(JWT.class)) {
            // 模拟JWT验证流程
            com.auth0.jwt.JWTVerifier.BaseVerification verification =
                    mock(com.auth0.jwt.JWTVerifier.BaseVerification.class);
            JWTVerifier verifier = mock(JWTVerifier.class);
            DecodedJWT decodedJWT = mock(DecodedJWT.class);

            mockedJWT.when(() -> JWT.require(any(Algorithm.class))).thenReturn(verification);
            when(verification.build()).thenReturn(verifier);

            // 正确构造TokenExpiredException
            Instant expiredInstant = Instant.now().minusSeconds(60);
            when(verifier.verify(expiredToken)).thenThrow(
                    new com.auth0.jwt.exceptions.TokenExpiredException("Token expired", expiredInstant));

            // 执行测试并验证异常
            assertThrows(com.auth0.jwt.exceptions.TokenExpiredException.class,
                    () -> JwtUtil.parseToken(expiredToken));
        }
    }

    @Test
    void genTokenAndParseToken_ShouldWorkTogether() {
        // 准备测试数据
        Map<String, Object> originalClaims = new HashMap<>();
        originalClaims.put("userId", 123);
        originalClaims.put("username", "testuser");

        // 生成token
        String token = JwtUtil.genToken(originalClaims);

        // 解析token
        Map<String, Object> parsedClaims = JwtUtil.parseToken(token);

        // 验证结果
        assertEquals(originalClaims, parsedClaims);
    }
}