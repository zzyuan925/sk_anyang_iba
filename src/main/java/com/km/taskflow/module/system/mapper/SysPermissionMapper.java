package com.km.taskflow.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.km.taskflow.module.system.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统权限 Mapper
 *
 * @author zzy
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 根据用户ID查询启用权限编码
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}