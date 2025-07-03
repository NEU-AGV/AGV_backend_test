package com.moxin.agvbackend.service.impl;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.UserMapper;
import com.moxin.agvbackend.pojo.entity.User;
import com.moxin.agvbackend.service.UserService;
import com.moxin.agvbackend.utils.ResultCode;
import com.moxin.agvbackend.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getProfile() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Long userId =  ((Integer) claims.get("id")).longValue();

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        return user;
    }
}
