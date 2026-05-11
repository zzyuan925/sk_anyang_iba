package com.km.taskflow.module.system.service;

import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.module.system.dto.PermissionCreateDTO;
import com.km.taskflow.module.system.dto.PermissionQueryDTO;
import com.km.taskflow.module.system.dto.PermissionUpdateCodeDTO;
import com.km.taskflow.module.system.dto.PermissionUpdateDTO;
import com.km.taskflow.module.system.vo.PermissionOptionVO;
import com.km.taskflow.module.system.vo.PermissionVO;

import java.util.List;

/**
 * @author zzy
 */
public interface SysPermissionService {

    /**
     * 分页查询权限
     */
    PageResult<PermissionVO> pagePermissions(PermissionQueryDTO queryDTO);
    /**
     * 获取权限详情
     */
    PermissionVO getPermissionById(Long id);
    /**
     * 创建权限
     */
    Long createPermission(PermissionCreateDTO createDTO);
    /**
     * 修改权限
     */
    void updatePermission(PermissionUpdateDTO updateDTO);
    /**
     * 删除权限
     */
    void deletePermission(Long id);
    /**
     * 下拉权限列表
     */
    List<PermissionOptionVO> listEnabledPermissionOptions();

    /**
     * 修改权限编码
     */
    void updatePermissionCode(Long id, PermissionUpdateCodeDTO updateCodeDTO);
}