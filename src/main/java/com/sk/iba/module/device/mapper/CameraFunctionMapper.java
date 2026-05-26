package com.sk.iba.module.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.iba.module.device.entity.CameraFunction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 摄像头功能关联 Mapper
 *
 * @author zzy
 */
public interface CameraFunctionMapper extends BaseMapper<CameraFunction> {

    /**
     * 批量插入摄像头功能关系
     */
    int insertBatch(@Param("list") List<CameraFunction> list);
}