package com.km.taskflow.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.km.taskflow.common.exception.BusinessException;
import com.km.taskflow.common.page.PageResult;
import com.km.taskflow.common.result.ResultCode;
import com.km.taskflow.module.system.dto.RoleCreateDTO;
import com.km.taskflow.module.system.dto.RoleQueryDTO;
import com.km.taskflow.module.system.dto.RoleUpdateDTO;
import com.km.taskflow.module.system.entity.SysRole;
import com.km.taskflow.module.system.entity.SysUserRole;
import com.km.taskflow.module.system.mapper.SysRoleMapper;
import com.km.taskflow.module.system.mapper.SysUserRoleMapper;
import com.km.taskflow.module.system.service.SysRoleService;
import com.km.taskflow.module.system.vo.RoleOptionVO;
import com.km.taskflow.module.system.vo.RoleVO;
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
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    
    private final SysUserRoleMapper sysUserRoleMapper;

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
            role.setStatus(1);
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
        if ("admin".equals(role.getRoleCode())) {
            throw new BusinessException("系统内置管理员角色不允许删除");
        }

        Long count = sysUserRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, id));

        if (count > 0) {
            throw new BusinessException("该角色已分配给用户，不能删除");
        }
        
        sysRoleMapper.deleteById(id);
    }

    @Override
    public List<RoleOptionVO> listEnabledRoleOptions() {
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getId));

        return roles.stream().map(role -> {
            RoleOptionVO vo = new RoleOptionVO();
            vo.setId(role.getId());
            vo.setRoleName(role.getRoleName());
            vo.setRoleCode(role.getRoleCode());
            return vo;
        }).toList();
    }

    private RoleVO toVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}