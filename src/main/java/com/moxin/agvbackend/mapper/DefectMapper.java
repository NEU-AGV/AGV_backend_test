package com.moxin.agvbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxin.agvbackend.mapper.provider.DefectSqlProvider;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import com.moxin.agvbackend.pojo.entity.Defect;
import com.moxin.agvbackend.pojo.vo.DefectVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DefectMapper extends BaseMapper<Defect> {

    @SelectProvider(type = DefectSqlProvider.class, method = "buildSelectWithTask")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "defect_id", property = "defectId"),
            @Result(column = "task_id", property = "taskId"),
            @Result(column = "task_name", property = "taskName"),
            @Result(column = "defect_type", property = "defectType"),
            @Result(column = "description", property = "description"),
            @Result(column = "location", property = "location"),
            @Result(column = "image_url", property = "imageUrl"),
            @Result(column = "severity", property = "severity"),
            @Result(column = "is_verified", property = "isVerified"),
            @Result(column = "current_status", property = "currentStatus"),
            @Result(column = "discoverer", property = "discoverer"),
            @Result(column = "discovery_time", property = "discoveryTime"),
            @Result(column = "discovery_method", property = "discoveryMethod"),
            @Result(column = "handler", property = "handler")
    })
    IPage<DefectVO> selectDefectWithTaskPage(IPage<?> page, @Param("query") DefectQuery query);
}
