package com.sk.iba.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sk.iba.common.constant.SystemConstants;
import com.sk.iba.common.enums.PermissionTypeEnum;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.system.dto.PermissionCreateDTO;
import com.sk.iba.module.system.dto.PermissionQueryDTO;
import com.sk.iba.module.system.dto.PermissionUpdateCodeDTO;
import com.sk.iba.module.system.dto.PermissionUpdateDTO;
import com.sk.iba.module.system.entity.SysPermission;
import com.sk.iba.module.system.entity.SysRolePermission;
import com.sk.iba.module.system.entity.SysUserRole;
import com.sk.iba.module.system.mapper.SysPermissionMapper;
import com.sk.iba.module.system.mapper.SysRolePermissionMapper;
import com.sk.iba.module.system.mapper.SysUserRoleMapper;
import com.sk.iba.module.system.service.SysPermissionService;
import com.sk.iba.module.system.vo.PermissionOptionVO;
import com.sk.iba.module.system.vo.PermissionTreeVO;
import com.sk.iba.module.system.vo.PermissionVO;
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
public class SysPermissionServiceImpl implements SysPermissionService {
    
    private final SysPermissionMapper sysPermissionMapper;

    private final SysRolePermissionMapper sysRolePermissionMapper;
    
    private final SysUserRoleMapper sysUserRoleMapper;

    private final LoginUserCacheService loginUserCacheService;

//    @Override
//    public PageResult<PermissionVO> pagePermissions(PermissionQueryDTO queryDTO) {
//        Page<SysPermission> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
//
//        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
//        wrapper.like(StringUtils.hasText(queryDTO.getPermissionName()), SysPermission::getPermissionName, queryDTO.getPermissionName())
//                .like(StringUtils.hasText(queryDTO.getPermissionCode()), SysPermission::getPermissionCode, queryDTO.getPermissionCode())
//                .eq(queryDTO.getPermissionType() != null, SysPermission::getPermissionType, queryDTO.getPermissionType())
//                .eq(queryDTO.getStatus() != null, SysPermission::getStatus, queryDTO.getStatus())
//                .orderByAsc(SysPermission::getParentId)
//                .orderByAsc(SysPermission::getId);
//
//        Page<SysPermission> permissionPage = sysPermissionMapper.selectPage(page, wrapper);
//
//        IPage<PermissionVO> voPage = permissionPage.convert(this::toVO);
//
//        return PageResult.of(voPage);
//    }

    @Override
    public PermissionVO getPermissionById(Long id) {
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }
        return toVO(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionCreateDTO createDTO) {
        String permissionName = createDTO.getPermissionName().trim();
        String permissionCode = createDTO.getPermissionCode().trim();
        Long parentId = createDTO.getParentId() == null
                ? SystemConstants.ROOT_PARENT_ID
                : createDTO.getParentId();
        if (!SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
            SysPermission parent = sysPermissionMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("父级权限不存在");
            }
        }
        
        Long count = sysPermissionMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermissionCode, permissionCode));

        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }
        

        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(createDTO, permission);
        permission.setPermissionName(permissionName);
        permission.setPermissionCode(permissionCode);
        permission.setParentId(parentId);
        if (permission.getStatus() == null) {
            permission.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(permission.getStatus())) {
            throw new BusinessException("权限状态不合法");
        }
        
        if (permission.getPermissionType() == null) {
            permission.setPermissionType(PermissionTypeEnum.API.getCode());
        }
        if (!PermissionTypeEnum.isValid(permission.getPermissionType())) {
            throw new BusinessException("权限类型不合法");
        }

        sysPermissionMapper.insert(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionUpdateDTO updateDTO) {
        SysPermission oldPermission = sysPermissionMapper.selectById(updateDTO.getId());
        if (oldPermission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }
        Long parentId = updateDTO.getParentId();
        if (parentId != null) {
            if (parentId.equals(updateDTO.getId())) {
                throw new BusinessException("父级权限不能选择自己");
            }

            if (!SystemConstants.ROOT_PARENT_ID.equals(updateDTO.getParentId())) {
                SysPermission parent = sysPermissionMapper.selectById(parentId);
                if (parent == null) {
                    throw new BusinessException("父级权限不存在");
                }
            }
        }
        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("状态不合法");
        }
        
        if (updateDTO.getPermissionType() != null
                && !PermissionTypeEnum.isValid(updateDTO.getPermissionType())) {
            throw new BusinessException("权限类型不合法");
        }
        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(updateDTO, permission);

        sysPermissionMapper.updateById(permission);
        clearUserCacheByPermissionId(updateDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }

        Long childCount = sysPermissionMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, id));

        if (childCount > 0) {
            throw new BusinessException("该权限存在子权限，不能删除");
        }

        Long usedCount = sysRolePermissionMapper.selectCount(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getPermissionId, id));

        if (usedCount > 0) {
            throw new BusinessException("该权限已分配给角色，不能删除");
        }

        sysPermissionMapper.deleteById(id);
    }

    @Override
    public List<PermissionOptionVO> listEnabledPermissionOptions() {
        List<SysPermission> allEnabledPermissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .eq(SysPermission::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByAsc(SysPermission::getParentId)
                        .orderByAsc(SysPermission::getId)
        );

        if (allEnabledPermissions.isEmpty()) {
            return List.of();
        }

        if (SecurityUtils.isSuperAdmin()) {
            return allEnabledPermissions.stream().map(this::toOptionVO).toList();
        }

        Set<String> currentPermissionCodes = SecurityUtils.getLoginUser().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (currentPermissionCodes.isEmpty()) {
            return List.of();
        }

        Map<Long, SysPermission> permissionMap = allEnabledPermissions.stream()
                .collect(Collectors.toMap(SysPermission::getId, permission -> permission));

        Set<Long> visiblePermissionIds = new HashSet<>();

        for (SysPermission permission : allEnabledPermissions) {
            if (!currentPermissionCodes.contains(permission.getPermissionCode())) {
                continue;
            }

            visiblePermissionIds.add(permission.getId());

            Long parentId = permission.getParentId();
            while (parentId != null && !SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
                SysPermission parent = permissionMap.get(parentId);
                if (parent == null) {
                    break;
                }

                visiblePermissionIds.add(parent.getId());
                parentId = parent.getParentId();
            }
        }

        return allEnabledPermissions.stream()
                .filter(permission -> visiblePermissionIds.contains(permission.getId()))
                .map(this::toOptionVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermissionCode(Long id, PermissionUpdateCodeDTO updateCodeDTO) {
        SysPermission permission = sysPermissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }

        String permissionCode = updateCodeDTO.getPermissionCode().trim();

        Long count = sysPermissionMapper.selectCount(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getPermissionCode, permissionCode)
                .ne(SysPermission::getId, id));

        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }

        SysPermission updatePermission = new SysPermission();
        updatePermission.setId(id);
        updatePermission.setPermissionCode(permissionCode);

        sysPermissionMapper.updateById(updatePermission);
        clearUserCacheByPermissionId(id);
    }

    @Override
    public List<PermissionTreeVO> treePermissions(PermissionQueryDTO queryDTO) {
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .orderByAsc(SysPermission::getParentId)
                        .orderByAsc(SysPermission::getId)
        );

        if (permissions.isEmpty()) {
            return List.of();
        }
        Map<Long, SysPermission> permissionMap = permissions.stream()
                .collect(Collectors.toMap(SysPermission::getId, permission -> permission));
        Set<Long> visibleIds = filterVisiblePermissionIds(permissions, permissionMap, queryDTO);

        List<PermissionTreeVO> voList = permissions.stream()
                .filter(permission -> visibleIds.contains(permission.getId()))
                .map(this::toTreeVO)
                .toList();

        Map<Long, PermissionTreeVO> voMap = voList.stream()
                .collect(Collectors.toMap(PermissionTreeVO::getId, vo -> vo));

        List<PermissionTreeVO> treeList = new ArrayList<>();

        for (PermissionTreeVO vo : voList) {
            Long parentId = vo.getParentId();

            if (parentId == null || SystemConstants.ROOT_PARENT_ID.equals(parentId) || !voMap.containsKey(parentId)) {
                treeList.add(vo);
                continue;
            }

            PermissionTreeVO parent = voMap.get(parentId);
            parent.getChildren().add(vo);
        }

        return treeList;
    }

    private Set<Long> filterVisiblePermissionIds(List<SysPermission> permissions,
                                                 Map<Long, SysPermission> permissionMap,
                                                 PermissionQueryDTO queryDTO) {
        if (!hasPermissionQueryCondition(queryDTO)) {
            return permissions.stream()
                    .map(SysPermission::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        Set<Long> visibleIds = new LinkedHashSet<>();

        for (SysPermission permission : permissions) {
            if (!matchPermissionQuery(permission, queryDTO)) {
                continue;
            }

            // 1. 命中的节点本身
            visibleIds.add(permission.getId());

            // 2. 命中子节点时，把父级一起带出来
            addParentPermissionIds(permission, permissionMap, visibleIds);

            // 3. 命中父节点时，把子级一起带出来
            addChildPermissionIds(permission.getId(), permissions, visibleIds);
        }

        return visibleIds;
    }

    private boolean hasPermissionQueryCondition(PermissionQueryDTO queryDTO) {
        if (queryDTO == null) {
            return false;
        }

        return StringUtils.hasText(queryDTO.getPermissionName())
                || StringUtils.hasText(queryDTO.getPermissionCode())
                || queryDTO.getPermissionType() != null
                || queryDTO.getStatus() != null;
    }

    private boolean matchPermissionQuery(SysPermission permission, PermissionQueryDTO queryDTO) {
        if (queryDTO == null) {
            return true;
        }

        if (StringUtils.hasText(queryDTO.getPermissionName())
                && !permission.getPermissionName().contains(queryDTO.getPermissionName())) {
            return false;
        }

        if (StringUtils.hasText(queryDTO.getPermissionCode())
                && !permission.getPermissionCode().contains(queryDTO.getPermissionCode())) {
            return false;
        }

        if (queryDTO.getPermissionType() != null
                && !queryDTO.getPermissionType().equals(permission.getPermissionType())) {
            return false;
        }

        if (queryDTO.getStatus() != null
                && !queryDTO.getStatus().equals(permission.getStatus())) {
            return false;
        }

        return true;
    }

    private void addParentPermissionIds(SysPermission permission,
                                        Map<Long, SysPermission> permissionMap,
                                        Set<Long> visibleIds) {
        Long parentId = permission.getParentId();

        while (parentId != null && !SystemConstants.ROOT_PARENT_ID.equals(parentId)) {
            SysPermission parent = permissionMap.get(parentId);
            if (parent == null) {
                break;
            }

            visibleIds.add(parent.getId());
            parentId = parent.getParentId();
        }
    }

    private void addChildPermissionIds(Long parentId,
                                       List<SysPermission> permissions,
                                       Set<Long> visibleIds) {
        for (SysPermission permission : permissions) {
            if (!parentId.equals(permission.getParentId())) {
                continue;
            }

            visibleIds.add(permission.getId());
            addChildPermissionIds(permission.getId(), permissions, visibleIds);
        }
    }

    private PermissionTreeVO toTreeVO(SysPermission permission) {
        PermissionTreeVO vo = new PermissionTreeVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
    
    private PermissionVO toVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private PermissionOptionVO toOptionVO(SysPermission permission) {
        PermissionOptionVO vo = new PermissionOptionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private void clearUserCacheByPermissionId(Long permissionId) {
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getPermissionId, permissionId)
        );

        List<Long> roleIds = rolePermissions.stream()
                .map(SysRolePermission::getRoleId)
                .distinct()
                .toList();

        if (roleIds.isEmpty()) {
            return;
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .in(SysUserRole::getRoleId, roleIds)
        );

        List<Long> userIds = userRoles.stream()
                .map(SysUserRole::getUserId)
                .distinct()
                .toList();

        loginUserCacheService.deleteLoginUsers(userIds);
    }
}