package com.sk.iba.module.system.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.system.dto.*;
import com.sk.iba.module.system.vo.PermissionVO;
import com.sk.iba.module.system.vo.RoleOptionVO;
import com.sk.iba.module.system.vo.RoleVO;

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
     * 下拉角色选项
     */
    List<RoleOptionVO> listEnabledRoleOptions();
    /**
     * 查询角色已绑定权限
     */
    List<PermissionVO> listRolePermissions(Long roleId);
    /**
     * 给角色重新分配权限
     */
    void assignPermissions(RoleAssignPermissionDTO assignPermissionDTO);
    /**
     * 修改角色编码
     */
    void updateRoleCode(Long id, RoleUpdateCodeDTO updateCodeDTO);
}