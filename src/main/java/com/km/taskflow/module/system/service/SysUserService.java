package com.km.taskflow.module.system.service;

import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.module.system.dto.UserCreateDTO;
import com.km.taskflow.module.system.dto.UserQueryDTO;
import com.km.taskflow.module.system.dto.UserUpdateDTO;
import com.km.taskflow.module.system.vo.UserVO;

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
}