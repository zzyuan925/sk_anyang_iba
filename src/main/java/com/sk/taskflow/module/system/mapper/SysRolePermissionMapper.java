package com.sk.taskflow.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.taskflow.module.system.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper
 *
 * @author zzy
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 批量插入角色权限关系
     */
    int insertBatch(@Param("list") List<SysRolePermission> list);
}