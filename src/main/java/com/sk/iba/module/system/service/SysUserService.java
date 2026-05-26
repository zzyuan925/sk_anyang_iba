package com.sk.iba.module.system.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.system.dto.*;
import com.sk.iba.module.system.vo.RegionVO;
import com.sk.iba.module.system.vo.RoleVO;
import com.sk.iba.module.system.vo.UserVO;

import java.util.List;

/**
 * @author zzy
 */
public interface SysUserService {

    /**
     * 分页查询用户
     */
    PageResult<UserVO> pageUsers(UserQueryDTO queryDTO);
    /**
     * 查询用户详情
     */
    UserVO getUserById(Long id);
    /**
     * 添加用户
     */
    Long createUser(UserCreateDTO createDTO);
    /**
     * 修改用户
     */
    void updateUser(UserUpdateDTO updateDTO);
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    /**
     * 查询用户已绑定角色
     */
    List<RoleVO> listUserRoles(Long userId);
    /**
     * 给用户重新分配角色
     */
    void assignRoles(UserAssignRoleDTO assignRoleDTO);

    /**
     * 修改用户名
     */
    void updateUsername(Long id, UserUpdateUsernameDTO updateUsernameDTO);

    /**
     * 修改自己的密码
     */
    void changePassword(UserChangePasswordDTO changePasswordDTO);

    /**
     * 管理员重置密码
     */
    void resetPassword(Long id, UserResetPasswordDTO resetPasswordDTO);
    /**
     * 查询用户已绑定区域
     */
    List<RegionVO> listUserRegions(Long userId);

    /**
     * 给用户重新分配区域
     */
    void assignRegions(UserAssignRegionDTO assignRegionDTO);
}