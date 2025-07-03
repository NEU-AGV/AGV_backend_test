package com.moxin.agvbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.vo.DefectVO;

public interface DefectService {

    IPage<DefectVO> list(DefectQuery query);

    void save(Defect defect);


    void updateStatusByDefectId(String defectId, String status);
}
