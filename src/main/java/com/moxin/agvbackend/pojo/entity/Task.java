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
@TableName("task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskId;                  // 业务编号，如 TASK-20250626-001
    private String taskName;
    private String taskType;               // 例行巡检 / 临时任务等
    private String priority;               // 高 / 中 / 低
    private String description;

    private String creator;                // 创建人姓名
    private String executor;               // 执行人
    private String helper;                 // 协助人员

    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;

    private String line;                   // 所属线路，例如 "1号线"
    private String startLocation;          // 起始里程标，例如 "K10"
    private String endLocation;
    private String scope;                  // 任务范围，例如 "全断面"
    private String status;                 // 状态，例如 "进行中"、"已完成"

    private Integer progress;              // 进度百分比
    private String result;                 // 执行结果摘要
    private Integer problemsFound;         // 缺陷发现数
    private LocalDateTime uploadTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
