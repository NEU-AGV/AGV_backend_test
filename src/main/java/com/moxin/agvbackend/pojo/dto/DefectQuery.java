package com.moxin.agvbackend.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefectQuery {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String taskName;  // 本类中不包含 taskName 字段，但你前端写了，我可以留给 future join
    private String defectType;
    private String severity;
    private String currentStatus;
    private String discoveryStart;
    private String discoveryEnd;
}
