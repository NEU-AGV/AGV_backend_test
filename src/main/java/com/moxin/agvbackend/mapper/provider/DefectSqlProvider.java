package com.moxin.agvbackend.mapper.provider;

import cn.hutool.core.util.StrUtil;
import com.moxin.agvbackend.pojo.dto.DefectQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class DefectSqlProvider {

    public String buildSelectWithTask(@Param("query") DefectQuery query) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.id, d.defect_id, d.task_id, d.defect_type, d.description, ")
                .append("d.location, d.image_url, d.severity, d.is_verified, d.current_status, ")
                .append("d.discoverer, d.discovery_time, d.discovery_method, d.handler, ")
                .append("t.task_name ")
                .append("FROM defect d ")
                .append("LEFT JOIN task t ON d.task_id = t.task_id ")
                .append("WHERE 1 = 1 ");

        if (StrUtil.isNotBlank(query.getDefectType())) {
            sql.append("AND d.defect_type LIKE CONCAT('%', #{query.defectType}, '%') ");
        }
        if (StrUtil.isNotBlank(query.getSeverity())) {
            sql.append("AND d.severity = #{query.severity} ");
        }
        if (StrUtil.isNotBlank(query.getCurrentStatus())) {
            sql.append("AND d.current_status = #{query.currentStatus} ");
        }
        if (StrUtil.isNotBlank(query.getDiscoveryStart())) {
            sql.append("AND d.discovery_time >= #{query.discoveryStart} ");
        }
        if (StrUtil.isNotBlank(query.getDiscoveryEnd())) {
            sql.append("AND d.discovery_time <= #{query.discoveryEnd} ");
        }

        return sql.toString();
    }
}

