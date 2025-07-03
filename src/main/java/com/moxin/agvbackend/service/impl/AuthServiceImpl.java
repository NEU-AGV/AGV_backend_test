package com.moxin.agvbackend.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.LoginLogMapper;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.dto.LoginDTO;
import com.moxin.agvbackend.pojo.dto.RegisterDTO;
import com.moxin.agvbackend.pojo.entity.LoginLog;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.service.AuthService;
import com.moxin.agvbackend.utils.BCryptUtil;
import com.moxin.agvbackend.utils.JwtUtil;
import com.moxin.agvbackend.utils.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final LoginLogMapper loginLogMapper;

    @Override
    public String login(LoginDTO dto, String ip, String userAgent) {
        // 1. 校验验证码
        String redisKey = "captcha:" + dto.getUuid();
        String codeInRedis = redisTemplate.opsForValue().get(redisKey);
        if (codeInRedis == null || !codeInRedis.equalsIgnoreCase(dto.getCaptcha())) {
            throw new AppException(ResultCode.FAIL, "验证码错误或已过期");
        }
        redisTemplate.delete(redisKey);

        // 2. 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", dto.getUsername())
                .last("LIMIT 1"));

        if (user == null || !BCryptUtil.matchPassword(dto.getPassword(), user.getPassword())) {
            throw new AppException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        if (!"live".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ResultCode.FAIL, "该账号已被禁用");
        }

        user.setLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        LoginLog log = LoginLog.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .ip(ip)
                .userAgent(userAgent)
                .loginTime(LocalDateTime.now())
                .build();
        loginLogMapper.insert(log);


        // 3. 构建 claims 手动传入
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail()
        );

        String token = JwtUtil.genToken(claims);
        redisTemplate.opsForValue().set("auth:token:" + token, token, 1, TimeUnit.DAYS);
        return token;
    }

    @Override
    public void register(RegisterDTO dto) {
        // 1. 校验邮箱验证码
        String redisKey = "mail:code:" + dto.getEmail();
        String codeInRedis = redisTemplate.opsForValue().get(redisKey);
        if (codeInRedis == null || !codeInRedis.equals(dto.getCode())) {
            throw new AppException(ResultCode.FAIL, "邮箱验证码错误或已过期");
        }

        // 2. 检查是否存在用户名或邮箱（用 count + QueryWrapper）
        Long countUsername = userMapper.selectCount(new QueryWrapper<User>().eq("username", dto.getUsername()));
        if (countUsername > 0) {
            throw new AppException(ResultCode.FAIL, "用户名已存在");
        }

        Long countEmail = userMapper.selectCount(new QueryWrapper<User>().eq("email", dto.getEmail()));
        if (countEmail > 0) {
            throw new AppException(ResultCode.FAIL, "邮箱已被注册");
        }

        // 3. 插入用户
        User user = User.builder()
                .username(dto.getUsername())
                .password(BCryptUtil.encryptPassword(dto.getPassword()))
                .email(dto.getEmail())
                .status("live")
                .createdAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);
        redisTemplate.delete(redisKey);
    }
}

