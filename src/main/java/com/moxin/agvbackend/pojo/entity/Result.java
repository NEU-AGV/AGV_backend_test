package com.moxin.agvbackend.pojo.entity;


import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class Result {
    private Integer code;
    private String message;
    private Object data;
}
