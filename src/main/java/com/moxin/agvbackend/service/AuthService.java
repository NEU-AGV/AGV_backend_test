package com.moxin.agvbackend.service;

import com.moxin.agvbackend.pojo.dto.LoginDTO;
import com.moxin.agvbackend.pojo.dto.RegisterDTO;

public interface AuthService {
    String login(LoginDTO dto, String ip, String userAgent);
    void register(RegisterDTO dto);
}