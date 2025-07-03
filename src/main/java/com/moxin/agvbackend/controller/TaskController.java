package com.moxin.agvbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxin.agvbackend.pojo.dto.TaskQueryDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.Task;
import com.moxin.agvbackend.service.TaskService;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public Result getTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            TaskQueryDTO queryDTO
    ) {
        IPage<Task> pageData = taskService.getTaskPage(page, pageSize, queryDTO);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("查询成功")
                .data(pageData)
                .build();
    }

    @DeleteMapping("/{id}")
    public Result deleteTask(@PathVariable Long id) {
        taskService.removeById(id);
        return Result.builder()
                .code(ResultCode.SUCCESS)
                .message("删除成功")
                .build();
    }

    @GetMapping("/export")
    public void exportTasks(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        taskService.exportTasks(params, response);
    }
}
