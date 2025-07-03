package com.moxin.agvbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moxin.agvbackend.pojo.dto.TaskQueryDTO;
import com.moxin.agvbackend.pojo.entity.Task;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Set;

public interface TaskService extends IService<Task> {

    IPage<Task> getTaskPage(Integer page, Integer pageSize, TaskQueryDTO dto);

    void exportTasks(Map<String, Object> params, HttpServletResponse response);

    Map<String, String> findNameMapByIds(Set<String> taskIds);
}
