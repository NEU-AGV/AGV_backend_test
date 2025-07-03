package com.moxin.agvbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxin.agvbackend.pojo.dto.TaskQueryDTO;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.entity.Task;
import com.moxin.agvbackend.service.TaskService;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void getTasks_ShouldReturnSuccessResult() {
        // 准备测试数据
        TaskQueryDTO queryDTO = new TaskQueryDTO();
        queryDTO.setTaskName("测试任务");
        IPage<Task> mockPage = mock(IPage.class);

        // 模拟行为
        when(taskService.getTaskPage(anyInt(), anyInt(), any(TaskQueryDTO.class)))
                .thenReturn(mockPage);

        // 调用方法
        Result result = taskController.getTasks(1, 10, queryDTO);

        // 验证结果
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("查询成功", result.getMessage());
        assertEquals(mockPage, result.getData());

        // 验证交互
        verify(taskService).getTaskPage(1, 10, queryDTO);
    }

    @Test
    void getTasks_WithDefaultParams_ShouldWork() {
        // 模拟行为
        IPage<Task> mockPage = mock(IPage.class);
        when(taskService.getTaskPage(anyInt(), anyInt(), isNull()))
                .thenReturn(mockPage);

        // 调用方法
        Result result = taskController.getTasks(1, 10, null);

        // 验证结果
        assertEquals(ResultCode.SUCCESS, result.getCode());
        verify(taskService).getTaskPage(1, 10, null);
    }

    @Test
    void deleteTask_ShouldReturnSuccessResult() {
        // 调用方法
        Long taskId = 1L;
        Result result = taskController.deleteTask(taskId);

        // 验证结果
        assertEquals(ResultCode.SUCCESS, result.getCode());
        assertEquals("删除成功", result.getMessage());

        // 验证交互
        verify(taskService).removeById(taskId);
    }

    @Test
    void exportTasks_ShouldCallServiceMethod() {
        // 准备测试数据
        Map<String, Object> params = Collections.singletonMap("status", "已完成");
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        // 调用方法
        taskController.exportTasks(params, mockResponse);

        // 验证交互
        verify(taskService).exportTasks(params, mockResponse);
    }
}