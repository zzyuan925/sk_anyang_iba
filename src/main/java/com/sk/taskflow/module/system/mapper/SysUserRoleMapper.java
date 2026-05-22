package com.sk.taskflow.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.taskflow.module.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author zzy
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    
    /**
     * 批量插入用户角色关系
     */
    int insertBatch(@Param("list") List<SysUserRole> list);
}