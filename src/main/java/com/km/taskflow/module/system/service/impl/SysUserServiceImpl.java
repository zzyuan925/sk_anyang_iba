package com.km.taskflow.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.km.taskflow.common.enums.StatusEnum;
import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.*;
import com.km.taskflow.module.system.entity.SysRole;
import com.km.taskflow.module.system.entity.SysUser;
import com.km.taskflow.module.system.entity.SysUserRole;
import com.km.taskflow.module.system.mapper.SysRoleMapper;
import com.km.taskflow.module.system.mapper.SysUserMapper;
import com.km.taskflow.module.system.mapper.SysUserRoleMapper;
import com.km.taskflow.module.system.service.SysUserService;
import com.km.taskflow.module.system.vo.RoleVO;
import com.km.taskflow.module.system.vo.UserVO;
import com.km.taskflow.security.LoginUserCacheService;
import com.km.taskflow.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    private final LoginUserCacheService loginUserCacheService;

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
        
        SysUser oldUser = sysUserMapper.selectById(updateDTO.getId());
        if (oldUser == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(updateDTO, user);

        sysUserMapper.updateById(user);
        loginUserCacheService.deleteLoginUser(updateDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

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
}