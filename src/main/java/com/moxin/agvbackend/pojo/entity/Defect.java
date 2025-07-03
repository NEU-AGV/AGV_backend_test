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
@TableName("defect")
public class Defect {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String defectId;               // eg. DEF-2025-38725
    private String taskId;                 // 关联任务ID（字符串）
    private String defectType;            // 裂缝、渗水等
    private String description;
    private String location;              // 缺陷位置，例如 K10+500
    private String imageUrl;

    private String severity;              // 高 / 中 / 低
    private Boolean isVerified;           // 是否已人工确认
    private String currentStatus;         // 当前处理状态
    private String discoverer;            // 发现人
    private LocalDateTime discoveryTime;
    private String discoveryMethod;       // 发现方式，如 智能识别
    private String handler;               // 处理人或班组

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
