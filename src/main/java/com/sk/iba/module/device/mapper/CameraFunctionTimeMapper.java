package com.sk.iba.module.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.iba.module.device.entity.CameraFunctionTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 摄像头功能运行时间段 Mapper
 *
 * @author zzy
 */
public interface CameraFunctionTimeMapper extends BaseMapper<CameraFunctionTime> {

    /**
     * 批量插入运行时间段
     */
    int insertBatch(@Param("list") List<CameraFunctionTime> list);
}