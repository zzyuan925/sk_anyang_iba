package com.sk.iba.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.iba.module.system.entity.SysUserRegion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户区域关联 Mapper
 *
 * @author zzy
 */
public interface SysUserRegionMapper extends BaseMapper<SysUserRegion> {

    /**
     * 批量插入用户区域关系
     */
    int insertBatch(@Param("list") List<SysUserRegion> list);
}