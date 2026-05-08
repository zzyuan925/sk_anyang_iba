package com.km.taskflow.module.system.service;

import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.module.system.dto.RoleCreateDTO;
import com.km.taskflow.module.system.dto.RoleQueryDTO;
import com.km.taskflow.module.system.dto.RoleUpdateDTO;
import com.km.taskflow.module.system.vo.RoleOptionVO;
import com.km.taskflow.module.system.vo.RoleVO;

import java.util.List;

/**
 * @author zzy
 */
public interface SysRoleService {

    /**
     * 分页查询角色
     */
    PageResult<RoleVO> pageRoles(RoleQueryDTO queryDTO);
    /**
     * 获取角色详情
     */
    RoleVO getRoleById(Long id);
    /**
     * 创建角色
     */
    Long createRole(RoleCreateDTO createDTO);
    /**
     * 修改角色
     */
    void updateRole(RoleUpdateDTO updateDTO);
    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 角色下拉选项
     */
    List<RoleOptionVO> listEnabledRoleOptions();
}