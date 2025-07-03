package com.moxin.agvbackend.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxin.agvbackend.exception.AppException;
import com.moxin.agvbackend.mapper.TaskMapper;
import com.moxin.agvbackend.pojo.dto.TaskQueryDTO;
import com.moxin.agvbackend.pojo.entity.Task;
import com.moxin.agvbackend.service.TaskService;
import com.moxin.agvbackend.utils.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public IPage<Task> getTaskPage(Integer page, Integer pageSize, TaskQueryDTO dto) {
        Page<Task> pageObj = Page.of(page, pageSize);
        LambdaQueryWrapper<Task> wrapper = Wrappers.lambdaQuery();

        if (StringUtils.isNotBlank(dto.getTaskName())) {
            wrapper.like(Task::getTaskName, dto.getTaskName());
        }

        if (StringUtils.isNotBlank(dto.getStatus())) {
            wrapper.eq(Task::getStatus, dto.getStatus());
        }

        if (StringUtils.isNotBlank(dto.getMonth())) {
            LocalDate start = LocalDate.parse(dto.getMonth() + "-01");
            LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
            wrapper.between(Task::getPlannedStartTime, start.atStartOfDay(), end.atTime(LocalTime.MAX));
        }


        wrapper.orderByDesc(Task::getCreatedAt);
        return taskMapper.selectPage(pageObj, wrapper);
    }


    @Override
    public void exportTasks(Map<String, Object> params, HttpServletResponse response) {
        List<Task> tasks = this.list(new LambdaQueryWrapper<Task>()); // 可以加筛选条件
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=tasks.xlsx");
            OutputStream out = response.getOutputStream();
            ExcelWriter writer = ExcelUtil.getWriter(true);
            writer.write(tasks, true);
            writer.flush(out, true);
            writer.close();
            IoUtil.close(out);
        } catch (IOException e) {
            throw new AppException(ResultCode.FAIL, "导出失败");
        }
    }


    @Override
    public Map<String, String> findNameMapByIds(Set<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) return Map.of();

        return taskMapper.selectList(
                new LambdaQueryWrapper<Task>().in(Task::getTaskId, taskIds)
        ).stream().collect(Collectors.toMap(Task::getTaskId, Task::getTaskName));
    }
}
