package com.moxin.agvbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxin.agvbackend.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
