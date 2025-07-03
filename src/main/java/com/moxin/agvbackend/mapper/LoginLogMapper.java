package com.moxin.agvbackend.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxin.agvbackend.pojo.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
