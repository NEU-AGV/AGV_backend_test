package com.moxin.agvbackend.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String realName;
    private Long departmentId;
    private String phone;
    @JsonIgnore
    private String password;
    private String email;
    private String role;
    private String avatarUrl;
    private String status;

    private LocalDateTime loginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
