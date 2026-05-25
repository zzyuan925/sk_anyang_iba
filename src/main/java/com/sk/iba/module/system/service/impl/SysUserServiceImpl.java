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
import com.sk.iba.module.system.entity.SysRole;
import com.sk.iba.module.system.entity.SysUser;
import com.sk.iba.module.system.entity.SysUserRole;
import com.sk.iba.module.system.mapper.SysRoleMapper;
import com.sk.iba.module.system.mapper.SysUserMapper;
import com.sk.iba.module.system.mapper.SysUserRoleMapper;
import com.sk.iba.module.system.service.SysUserService;
import com.sk.iba.module.system.vo.RoleVO;
import com.sk.iba.module.system.vo.UserVO;
import com.sk.iba.security.LoginUserCacheService;
import com.sk.iba.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    
    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final PasswordEncoder passwordEncoder;

    private final LoginUserCacheService loginUserCacheService;

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUser::getUsername, queryDTO.getUsername())
                .like(StringUtils.hasText(queryDTO.getRealName()), SysUser::getRealName, queryDTO.getRealName())
                .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus());

        Long currentUserId = SecurityUtils.getUserId();
        if (!SecurityUtils.isSuperAdmin()) {
            wrapper.and(w -> w
                    .eq(SysUser::getCreateBy, currentUserId)
                    .or()
                    .eq(SysUser::getId, currentUserId)
            );
        }

        wrapper.orderByDesc(SysUser::getCreateTime);
        
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
        checkUserDataPermission(user);
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
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        if (user.getStatus() == null) {
            user.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(user.getStatus())) {
            throw new BusinessException("用户状态不合法");
        }

        sysUserMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateDTO updateDTO) {
        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("用户状态不合法");
        }
        if (SystemConstants.ADMIN_USER_ID.equals(updateDTO.getId())
                && updateDTO.getStatus() != null
                && !StatusEnum.isEnabled(updateDTO.getStatus())) {
            throw new BusinessException("超级管理员不允许禁用");
        }
        
        SysUser oldUser = sysUserMapper.selectById(updateDTO.getId());
        if (oldUser == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        
        checkUserDataPermission(oldUser);
        
        SysUser user = new SysUser();
        BeanUtils.copyProperties(updateDTO, user);

        sysUserMapper.updateById(user);

        // 状态改变了才清理缓存
        if (isLoginStateChanged(oldUser, updateDTO)) {
            loginUserCacheService.deleteLoginUser(updateDTO.getId());
        }
    }
    
    private boolean isLoginStateChanged(SysUser oldUser, UserUpdateDTO updateDTO) {
        if (updateDTO.getStatus() != null && !updateDTO.getStatus().equals(oldUser.getStatus())) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (SystemConstants.ADMIN_USER_ID.equals(id)) {
            throw new BusinessException("超级管理员不允许删除");
        }
        checkUserDataPermission(user);

        sysUserMapper.deleteById(id);
        loginUserCacheService.deleteLoginUser(id);

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
    }

    @Override
    public List<RoleVO> listUserRoles(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        checkUserDataPermission(user);
        
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
        if (!StatusEnum.isEnabled(user.getStatus())) {
            throw new BusinessException("不能给禁用用户分配角色");
        }

        checkUserRoleAssignTargetPermission(user);

        Set<Long> uniqueRoleIds = assignRoleDTO.getRoleIds() == null ? Set.of() : new HashSet<>(assignRoleDTO.getRoleIds());
        
        if (!uniqueRoleIds.isEmpty()) {
            List<SysRole> roles = sysRoleMapper.selectBatchIds(uniqueRoleIds);

            if (roles.size() != uniqueRoleIds.size()) {
                throw new BusinessException("存在无效角色");
            }

            boolean hasDisabledRole = roles.stream()
                    .anyMatch(role -> !StatusEnum.isEnabled(role.getStatus()));
            if (hasDisabledRole) {
                throw new BusinessException("不能分配已禁用角色");
            }
            
            checkAssignableRoles(roles);
        }

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (!uniqueRoleIds.isEmpty()) {
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
        
        // 不管是清空角色，还是重新分配角色，都要清理用户登录缓存
        loginUserCacheService.deleteLoginUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsername(Long id, UserUpdateUsernameDTO updateUsernameDTO) {
        
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        checkUserDataPermission(user);
        
        String username = updateUsernameDTO.getUsername().trim();

        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .ne(SysUser::getId, id));

        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setUsername(username);

        sysUserMapper.updateById(updateUser);
        loginUserCacheService.deleteLoginUser(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtils.getUserId();

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能和旧密码相同");
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));

        sysUserMapper.updateById(updateUser);
        loginUserCacheService.deleteLoginUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, UserResetPasswordDTO resetPasswordDTO) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        checkUserDataPermission(user);
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));

        sysUserMapper.updateById(updateUser);
        loginUserCacheService.deleteLoginUser(id);
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

    private void checkUserDataPermission(SysUser user) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Long currentUserId = SecurityUtils.getUserId();

        boolean self = currentUserId.equals(user.getId());
        boolean createdByMe = currentUserId.equals(user.getCreateBy());

        if (!self && !createdByMe) {
            throw new BusinessException("无权操作该用户");
        }
    }

    private void checkAssignableRoles(List<SysRole> roles) {
        if (SecurityUtils.isSuperAdmin() || roles == null || roles.isEmpty()) {
            return;
        }

        Long currentUserId = SecurityUtils.getUserId();

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, currentUserId)
        );

        Set<Long> currentUserRoleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());

        boolean hasNoPermissionRole = roles.stream().anyMatch(role -> {
            boolean createdByMe = currentUserId.equals(role.getCreateBy());
            boolean ownedByMe = currentUserRoleIds.contains(role.getId());
            return !createdByMe && !ownedByMe;
        });

        if (hasNoPermissionRole) {
            throw new BusinessException("不能分配无权管理的角色");
        }
    }

    private void checkUserRoleAssignTargetPermission(SysUser user) {
        if (SecurityUtils.isSuperAdmin()) {
            return;
        }

        Long currentUserId = SecurityUtils.getUserId();

        if (!currentUserId.equals(user.getCreateBy())) {
            throw new BusinessException("无权给该用户分配角色");
        }
    }
}