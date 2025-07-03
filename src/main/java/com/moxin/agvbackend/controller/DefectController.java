package com.moxin.agvbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.dto.StatusDTO;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.entity.Result;
import com.moxin.agvbackend.pojo.vo.DefectVO;
import com.moxin.agvbackend.service.DefectService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;

    @GetMapping
    public Result list(DefectQuery query) {
        IPage<DefectVO> page = defectService.list(query);
        Map<String, Object> data = Map.of(
                "total", page.getTotal(),
                "list", page.getRecords()
        );
        return Result.builder()
                .code(200)
                .message("获取成功")
                .data(data)
                .build();
    }

    @PostMapping
    public Result create(@RequestBody Defect defect) {
        defectService.save(defect);
        return Result.builder()
                .code(200)
                .message("新增成功")
                .build();
    }

    @PutMapping("/{defectId}/status")
    public Result updateStatus(@PathVariable String defectId, @RequestBody StatusDTO dto) {
        defectService.updateStatusByDefectId(defectId, dto.getStatus());
        return Result.builder()
                .code(200)
                .message("状态更新成功")
                .build();
    }

    @Data
    public static class StatusUpdateRequest {
        private String status;
    }
}
