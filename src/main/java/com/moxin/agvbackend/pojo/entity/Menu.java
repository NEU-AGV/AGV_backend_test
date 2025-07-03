package com.moxin.agvbackend.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("menu")
public class Menu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String menuName;
    private String menuType;     // M, C, F
    private String icon;
    private String path;
    private String component;
    private String perms;
    private Integer orderNum;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
