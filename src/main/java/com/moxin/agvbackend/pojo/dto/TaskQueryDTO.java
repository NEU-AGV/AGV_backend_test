package com.moxin.agvbackend.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueryDTO {
    private String taskName;
    private String status;
    private String month;
    private String executor;
    private String line;
}
