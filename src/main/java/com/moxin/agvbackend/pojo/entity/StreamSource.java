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
@TableName("stream_source")
public class StreamSource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;          // 例如：前门摄像头
    private String url;           // http://xxx:8000/live.mjpg
    private String type;          // mjpeg / rtsp / ws
    private String description;   // 说明

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
