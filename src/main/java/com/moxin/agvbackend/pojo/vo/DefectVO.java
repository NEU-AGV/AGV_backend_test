package com.moxin.agvbackend.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DefectVO {
    private Long id;
    private String defectId;
    private String taskId;
    private String taskName;           // ✅ 来自任务表
    private String defectType;
    private String description;
    private String location;
    private String imageUrl;
    private String severity;
    private Boolean isVerified;
    private String currentStatus;
    private String discoverer;
    private LocalDateTime discoveryTime;
    private String discoveryMethod;
    private String handler;
}
