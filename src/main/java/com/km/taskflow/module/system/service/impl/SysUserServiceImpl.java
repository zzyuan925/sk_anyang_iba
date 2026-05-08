package com.km.taskflow.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.UserAssignRoleDTO;
import com.km.taskflow.module.system.dto.UserCreateDTO;
import com.km.taskflow.module.system.dto.UserQueryDTO;
import com.km.taskflow.module.system.dto.UserUpdateDTO;
import com.km.taskflow.module.system.entity.SysRole;
import com.km.taskflow.module.system.entity.SysUser;
import com.km.taskflow.module.system.entity.SysUserRole;
import com.km.taskflow.module.system.mapper.SysRoleMapper;
import com.km.taskflow.module.system.mapper.SysUserMapper;
import com.km.taskflow.module.system.mapper.SysUserRoleMapper;
import com.km.taskflow.module.system.service.SysUserService;
import com.km.taskflow.module.system.vo.RoleVO;
import com.km.taskflow.module.system.vo.UserVO;
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
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    
    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUser::getUsername, queryDTO.getUsername())
                .like(StringUtils.hasText(queryDTO.getRealName()), SysUser::getRealName, queryDTO.getRealName())
                .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
                .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

        IPage<UserVO> voPage = userPage.convert(this::toVO);

        return PageResult.of(voPage);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDTO createDTO) {
        String username = createDTO.getUsername().trim();
        long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(createDTO, user);

        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateDTO updateDTO) {
        SysUser oldUser = sysUserMapper.selectById(updateDTO.getId());
        if (oldUser == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(updateDTO, user);

        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        sysUserMapper.deleteById(id);

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
    }

    @Override
    public List<RoleVO> listUserRoles(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId,roleIds)
                .orderByAsc(SysRole::getId));

        return roles.stream().map(this::toRoleVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(UserAssignRoleDTO assignRoleDTO) {
        Long userId = assignRoleDTO.getUserId();

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("不能给禁用用户分配角色");
        }

        Set<Long> uniqueRoleIds = assignRoleDTO.getRoleIds() == null ? Set.of() : new HashSet<>(assignRoleDTO.getRoleIds());
        
        if (!uniqueRoleIds.isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectBatchIds(uniqueRoleIds);

            if (roles.size() != uniqueRoleIds.size()) {
                throw new BusinessException("存在无效角色");
            }

            boolean hasDisabledRole = roles.stream()
                    .anyMatch(role -> role.getStatus() == null || role.getStatus() != 1);
            if (hasDisabledRole) {
                throw new BusinessException("不能分配已禁用角色");
            }
        }

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (uniqueRoleIds.isEmpty()) {
            return;
        }

        List<SysUserRole> userRoleList = uniqueRoleIds.stream()
                .map(roleId -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .toList();

        sysUserRoleMapper.insertBatch(userRoleList);
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
    private RoleVO toRoleVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}