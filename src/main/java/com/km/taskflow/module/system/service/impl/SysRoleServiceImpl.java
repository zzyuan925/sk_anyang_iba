package com.km.taskflow.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.km.taskflow.common.constant.SystemConstants;
import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.*;
import com.km.taskflow.module.system.entity.SysPermission;
import com.km.taskflow.module.system.entity.SysRole;
import com.km.taskflow.module.system.entity.SysRolePermission;
import com.km.taskflow.module.system.entity.SysUserRole;
import com.km.taskflow.module.system.mapper.SysPermissionMapper;
import com.km.taskflow.module.system.mapper.SysRoleMapper;
import com.km.taskflow.module.system.mapper.SysRolePermissionMapper;
import com.km.taskflow.module.system.mapper.SysUserRoleMapper;
import com.km.taskflow.module.system.service.SysRoleService;
import com.km.taskflow.module.system.vo.PermissionVO;
import com.km.taskflow.module.system.vo.RoleOptionVO;
import com.km.taskflow.module.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    
    private final SysUserRoleMapper sysUserRoleMapper;

    private final SysPermissionMapper sysPermissionMapper;

    private final SysRolePermissionMapper sysRolePermissionMapper;
    @Override
    public PageResult<RoleVO> pageRoles(RoleQueryDTO queryDTO) {
        Page<SysRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()), SysRole::getRoleName, queryDTO.getRoleName())
                .like(StringUtils.hasText(queryDTO.getRoleCode()), SysRole::getRoleCode, queryDTO.getRoleCode())
                .eq(queryDTO.getStatus() != null, SysRole::getStatus, queryDTO.getStatus())
                .orderByDesc(SysRole::getCreateTime);

        Page<SysRole> rolePage = sysRoleMapper.selectPage(page, wrapper);

        IPage<RoleVO> voPage = rolePage.convert(this::toVO);

        return PageResult.of(voPage);
    }

    @Override
    public RoleVO getRoleById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        return toVO(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO createDTO) {
        String roleName = createDTO.getRoleName().trim();
        String roleCode = createDTO.getRoleCode().trim();

        long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode));

        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(createDTO, role);
        role.setRoleName(roleName);
        role.setRoleCode(roleCode);

        if (role.getStatus() == null) {
            role.setStatus(SystemConstants.STATUS_ENABLED);
        }

        sysRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO updateDTO) {
        SysRole oldRole = sysRoleMapper.selectById(updateDTO.getId());
        if (oldRole == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(updateDTO, role);

        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        // 先简单限制：系统内置 admin 角色不允许删除
        if (SystemConstants.ADMIN_ROLE_CODE.equals(role.getRoleCode())) {
            throw new BusinessException("系统内置管理员角色不允许删除");
        }

        Long userCount = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, id));

        if (userCount > 0) {
            throw new BusinessException("该角色已分配给用户，不能删除");
        }

        Long permissionCount = sysRolePermissionMapper.selectCount(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));

        if (permissionCount > 0) {
            throw new BusinessException("该角色已分配权限，不能删除");
        }
        
        sysRoleMapper.deleteById(id);
    }

    @Override
    public List<RoleOptionVO> listEnabledRoleOptions() {
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, SystemConstants.STATUS_ENABLED)
                .orderByAsc(SysRole::getId));

        return roles.stream().map(this::toOptionVO).toList();
    }

    @Override
    public List<PermissionVO> listRolePermissions(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));

        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .toList();

        if (permissionIds.isEmpty()) {
            return List.of();
        }

        List<SysPermission> permissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permissionIds)
                .orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getId));

        return permissions.stream().map(this::toPermissionVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RoleAssignPermissionDTO assignPermissionDTO) {
        Long roleId = assignPermissionDTO.getRoleId();

        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        if (!SystemConstants.STATUS_ENABLED.equals(role.getStatus())) {
            throw new BusinessException("不能给禁用角色分配权限");
        }

        Set<Long> uniquePermissionIds = assignPermissionDTO.getPermissionIds() == null ? Set.of() : new HashSet<>(assignPermissionDTO.getPermissionIds());

        if (!uniquePermissionIds.isEmpty()) {
            List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(uniquePermissionIds);

            if (permissions.size() != uniquePermissionIds.size()) {
                throw new BusinessException("存在无效权限");
            }

            boolean hasDisabledPermission = permissions.stream()
                    .anyMatch(permission -> !SystemConstants.STATUS_ENABLED.equals(permission.getStatus()));

            if (hasDisabledPermission) {
                throw new BusinessException("不能分配已禁用权限");
            }
        }

        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));

        if (uniquePermissionIds.isEmpty()) {
            return;
        }

        List<SysRolePermission> rolePermissionList = uniquePermissionIds.stream()
                .map(permissionId -> {
                    SysRolePermission rolePermission = new SysRolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setPermissionId(permissionId);
                    return rolePermission;
                })
                .toList();

        sysRolePermissionMapper.insertBatch(rolePermissionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleCode(Long id, RoleUpdateCodeDTO updateCodeDTO) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        if (SystemConstants.ADMIN_ROLE_CODE.equals(role.getRoleCode())) {
            throw new BusinessException("系统内置管理员角色编码不允许修改");
        }

        String roleCode = updateCodeDTO.getRoleCode().trim();

        Long count = sysRoleMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode)
                .ne(SysRole::getId, id));

        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole updateRole = new SysRole();
        updateRole.setId(id);
        updateRole.setRoleCode(roleCode);

        sysRoleMapper.updateById(updateRole);
    }

    private PermissionVO toPermissionVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
    
    private RoleOptionVO toOptionVO(SysRole role){
        RoleOptionVO vo = new RoleOptionVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}