package com.sk.iba.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.constant.SystemConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.system.dto.*;
import com.sk.iba.module.system.entity.SysPermission;
import com.sk.iba.module.system.entity.SysRole;
import com.sk.iba.module.system.entity.SysRolePermission;
import com.sk.iba.module.system.entity.SysUserRole;
import com.sk.iba.module.system.mapper.SysPermissionMapper;
import com.sk.iba.module.system.mapper.SysRoleMapper;
import com.sk.iba.module.system.mapper.SysRolePermissionMapper;
import com.sk.iba.module.system.mapper.SysUserRoleMapper;
import com.sk.iba.module.system.service.SysRoleService;
import com.sk.iba.module.system.vo.PermissionVO;
import com.sk.iba.module.system.vo.RoleOptionVO;
import com.sk.iba.module.system.vo.RoleVO;
import com.sk.iba.security.LoginUserCacheService;
import com.sk.iba.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    private final LoginUserCacheService loginUserCacheService;
    
    @Override
    public PageResult<RoleVO> pageRoles(RoleQueryDTO queryDTO) {
        Page<SysRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getRoleName()), SysRole::getRoleName, queryDTO.getRoleName())
                .like(StringUtils.hasText(queryDTO.getRoleCode()), SysRole::getRoleCode, queryDTO.getRoleCode())
                .eq(queryDTO.getStatus() != null, SysRole::getStatus, queryDTO.getStatus());

        if (!SecurityUtils.isSuperAdmin()) {
            wrapper.eq(SysRole::getCreateBy, SecurityUtils.getUserId());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        
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
        checkRoleDataPermission(role);
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
            role.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(role.getStatus())) {
            throw new BusinessException("角色状态不合法");
        }

        sysRoleMapper.insert(role);
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO updateDTO) {
        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("角色状态不合法");
        }
        
        SysRole oldRole = sysRoleMapper.selectById(updateDTO.getId());
        if (oldRole == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        checkRoleDataPermission(oldRole);
        
        SysRole role = new SysRole();
        BeanUtils.copyProperties(updateDTO, role);

        sysRoleMapper.updateById(role);
        clearUserCacheByRoleId(updateDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        checkRoleDataPermission(role);
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
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, StatusEnum.ENABLED.getCode());

        if (!SecurityUtils.isSuperAdmin()) {
            Long currentUserId = SecurityUtils.getUserId();

            // 当前用户拥有的角色
            List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                    new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getUserId, currentUserId)
            );

            Set<Long> currentUserRoleIds = userRoles.stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toSet());

            if (currentUserRoleIds.isEmpty()) {
                wrapper.eq(SysRole::getCreateBy, currentUserId);
            } else {
                wrapper.and(w -> w
                        .eq(SysRole::getCreateBy, currentUserId)
                        .or()
                        .in(SysRole::getId, currentUserRoleIds)
                );
            }
        }
        
        wrapper.orderByAsc(SysRole::getId);
        
        List<SysRole> roles = sysRoleMapper.selectList(wrapper);
        return roles.stream().map(this::toOptionVO).toList();
    }

    @Override
    public List<PermissionVO> listRolePermissions(Long roleId) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        checkRoleDataPermission(role);

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
        checkRoleDataPermission(role);
        if (!StatusEnum.isEnabled(role.getStatus())) {
            throw new BusinessException("不能给禁用角色分配权限");
        }

        Set<Long> uniquePermissionIds = assignPermissionDTO.getPermissionIds() == null
                ? Collections.emptySet()
                : new HashSet<>(assignPermissionDTO.getPermissionIds());

        Set<Long> permissionIds = fillParentPermissionIds(uniquePermissionIds);
        checkAssignablePermissions(permissionIds);

        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId));

        if (!permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissionList = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .toList();

            sysRolePermissionMapper.insertBatch(rolePermissionList);
        }

        // 不管是清空权限，还是重新分配权限，都要清理拥有该角色的用户缓存
        clearUserCacheByRoleId(roleId);
    }

    // 补齐父权限
    private Set<Long> fillParentPermissionIds(Collection<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptySet();
        }

        // 一次性查出全部启用、未删除权限
        List<SysPermission> allPermissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getStatus, StatusEnum.ENABLED.getCode())
        );

        Map<Long, SysPermission> permissionMap = allPermissions.stream()
                .collect(Collectors.toMap(SysPermission::getId, permission -> permission));

        for (Long permissionId : permissionIds) {
            if (!permissionMap.containsKey(permissionId)) {
                throw new BusinessException("存在无效、禁用或已删除的权限");
            }
        }

        Set<Long> finalPermissionIds = new HashSet<>(permissionIds);

        for (Long permissionId : permissionIds) {
            SysPermission permission = permissionMap.get(permissionId);

            Long parentId = permission.getParentId();
            while (parentId != null && parentId != 0L) {
                SysPermission parent = permissionMap.get(parentId);
                if (parent == null) {
                    throw new BusinessException("权限父级不存在、已禁用或已删除");
                }

                finalPermissionIds.add(parent.getId());
                parentId = parent.getParentId();
            }
        }

        return finalPermissionIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleCode(Long id, RoleUpdateCodeDTO updateCodeDTO) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        checkRoleDataPermission(role);

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
    /**
     * 清理拥有指定角色的用户登录缓存
     */
    private void clearUserCacheByRoleId(Long roleId) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId));

        List<Long> userIds = userRoles.stream()
                .map(SysUserRole::getUserId)
                .toList();

        loginUserCacheService.deleteLoginUsers(userIds);
    }

    private void checkRoleDataPermission(SysRole role) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Long currentUserId = SecurityUtils.getUserId();

        if (!currentUserId.equals(role.getCreateBy())) {
            throw new BusinessException("无权操作该角色");
        }
    }

    private void checkAssignablePermissions(Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty() || SecurityUtils.isSuperAdmin()) {
            return;
        }

        Set<String> currentPermissionCodes = SecurityUtils.getLoginUser().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (currentPermissionCodes.isEmpty()) {
            throw new BusinessException("不能分配无权管理的权限");
        }

        List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permissionIds);

        boolean hasNoPermission = permissions.stream()
                .anyMatch(permission -> !currentPermissionCodes.contains(permission.getPermissionCode()));

        if (hasNoPermission) {
            throw new BusinessException("不能分配超出自身范围的权限");
        }
    }
    
}