package com.km.taskflow.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.km.taskflow.common.constant.SystemConstants;
import com.km.taskflow.common.enums.PermissionTypeEnum;
import com.km.taskflow.common.enums.StatusEnum;
import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.PermissionCreateDTO;
import com.km.taskflow.module.system.dto.PermissionQueryDTO;
import com.km.taskflow.module.system.dto.PermissionUpdateCodeDTO;
import com.km.taskflow.module.system.dto.PermissionUpdateDTO;
import com.km.taskflow.module.system.entity.SysPermission;
import com.km.taskflow.module.system.entity.SysRolePermission;
import com.km.taskflow.module.system.entity.SysUserRole;
import com.km.taskflow.module.system.mapper.SysPermissionMapper;
import com.km.taskflow.module.system.mapper.SysRolePermissionMapper;
import com.km.taskflow.module.system.mapper.SysUserRoleMapper;
import com.km.taskflow.module.system.service.SysPermissionService;
import com.km.taskflow.module.system.vo.PermissionOptionVO;
import com.km.taskflow.module.system.vo.PermissionVO;
import com.km.taskflow.security.LoginUserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

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

    @Override
    public PageResult<PermissionVO> pagePermissions(PermissionQueryDTO queryDTO) {
        Page<SysPermission> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getPermissionName()), SysPermission::getPermissionName, queryDTO.getPermissionName())
                .like(StringUtils.hasText(queryDTO.getPermissionCode()), SysPermission::getPermissionCode, queryDTO.getPermissionCode())
                .eq(queryDTO.getPermissionType() != null, SysPermission::getPermissionType, queryDTO.getPermissionType())
                .eq(queryDTO.getStatus() != null, SysPermission::getStatus, queryDTO.getStatus())
                .orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getId);

        Page<SysPermission> permissionPage = sysPermissionMapper.selectPage(page, wrapper);

        IPage<PermissionVO> voPage = permissionPage.convert(this::toVO);

        return PageResult.of(voPage);
    }

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
        List<SysPermission> permissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getStatus, StatusEnum.ENABLED.getCode())
                .orderByAsc(SysPermission::getParentId)
                .orderByAsc(SysPermission::getId));

        return permissions.stream().map(this::toOptionVO).toList();
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