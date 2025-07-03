package com.moxin.agvbackend.controller;

import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.StreamSource;
import com.moxin.agvbackend.service.StreamSourceService;
import com.moxin.agvbackend.utils.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stream")
public class StreamController {

    @Autowired
    private StreamSourceService streamSourceService;

    @GetMapping("/list")
    public Result listSources() {
        List<StreamSource> list = streamSourceService.listAll();
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("获取成功")
                .data(list)
                .build();
    }

    @GetMapping("/source")
    public Result getDefaultStream() {
        // 示例：默认取第一个，或你可以加个配置项来指定
        List<StreamSource> list = streamSourceService.listAll();
        if (list.isEmpty()) throw new AppException(ResultCode.FAIL, "暂无可用视频源");
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("获取成功")
                .data(list.get(0).getUrl())
                .build();
    }
}
