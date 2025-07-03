package com.moxin.agvbackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.mapper.DefectMapper;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.vo.DefectVO;
import com.moxin.agvbackend.service.DefectService;
import com.moxin.agvbackend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements DefectService {

    private final DefectMapper defectMapper;
    private final TaskService taskService;

    @Override
    public IPage<DefectVO> list(DefectQuery query) {
        IPage<DefectVO> page = new Page<>(query.getPage(), query.getPageSize());
        return defectMapper.selectDefectWithTaskPage(page, query);
    }

    @Override
    public void save(Defect defect) {
        defect.setCreatedAt(LocalDateTime.now());
        defect.setUpdatedAt(LocalDateTime.now());
        defectMapper.insert(defect);
    }

    @Override
    public void updateStatusByDefectId(String defectId, String status) {
        LambdaQueryWrapper<Defect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Defect::getDefectId, defectId);

        Defect d = defectMapper.selectOne(wrapper);
        if (d != null) {
            d.setCurrentStatus(status);
            d.setUpdatedAt(LocalDateTime.now());
            defectMapper.updateById(d);
        }
    }

}
