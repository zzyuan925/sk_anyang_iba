package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.utils.DeployPathUtils;
import com.sk.iba.module.device.dto.AlgorithmServerCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmServerQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmServerUpdateDTO;
import com.sk.iba.module.device.entity.AlgorithmRuntime;
import com.sk.iba.module.device.entity.AlgorithmServer;
import com.sk.iba.module.device.mapper.AlgorithmRuntimeMapper;
import com.sk.iba.module.device.mapper.AlgorithmServerMapper;
import com.sk.iba.module.device.service.AlgorithmServerService;
import com.sk.iba.module.device.vo.AlgorithmServerOptionVO;
import com.sk.iba.module.device.vo.AlgorithmServerVO;
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
public class AlgorithmServerServiceImpl implements AlgorithmServerService {

    private final AlgorithmServerMapper algorithmServerMapper;

    private final AlgorithmRuntimeMapper algorithmRuntimeMapper;

    @Override
    public PageResult<AlgorithmServerVO> pageServers(AlgorithmServerQueryDTO queryDTO) {
        Page<AlgorithmServer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AlgorithmServer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getServerName()), AlgorithmServer::getServerName, queryDTO.getServerName())
                .like(StringUtils.hasText(queryDTO.getIp()), AlgorithmServer::getIp, queryDTO.getIp())
                .eq(queryDTO.getStatus() != null, AlgorithmServer::getStatus, queryDTO.getStatus())
                .orderByDesc(AlgorithmServer::getCreateTime);

        Page<AlgorithmServer> serverPage = algorithmServerMapper.selectPage(page, wrapper);
        IPage<AlgorithmServerVO> voPage = serverPage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public AlgorithmServerVO getServerById(Long id) {
        AlgorithmServer server = algorithmServerMapper.selectById(id);
        if (server == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法服务器不存在");
        }

        return toVO(server);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createServer(AlgorithmServerCreateDTO createDTO) {
        String serverName = createDTO.getServerName().trim();
        String account = createDTO.getAccount().trim();
        String password = createDTO.getPassword().trim();
        String ip = createDTO.getIp().trim();
        String deployPath = DeployPathUtils.normalize(createDTO.getDeployPath());

        if (!DeployPathUtils.isValidDeployPath(deployPath)) {
            throw new BusinessException("部署地址格式不合法，请填写 Ubuntu/Linux 绝对路径或 Windows 绝对路径");
        }

        if (createDTO.getStatus() == null) {
            createDTO.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(createDTO.getStatus())) {
            throw new BusinessException("服务器状态不合法");
        }

        checkDuplicate(serverName, ip, createDTO.getPort(), null);

        AlgorithmServer server = new AlgorithmServer();
        BeanUtils.copyProperties(createDTO, server);
        server.setServerName(serverName);
        server.setAccount(account);
        server.setPassword(password);
        server.setIp(ip);
        server.setDeployPath(deployPath);

        algorithmServerMapper.insert(server);
        return server.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateServer(AlgorithmServerUpdateDTO updateDTO) {
        AlgorithmServer oldServer = algorithmServerMapper.selectById(updateDTO.getId());
        if (oldServer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法服务器不存在");
        }

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("服务器状态不合法");
        }

        String serverName = oldServer.getServerName();
        if (updateDTO.getServerName() != null) {
            if (!StringUtils.hasText(updateDTO.getServerName())) {
                throw new BusinessException("服务器名不能为空");
            }
            serverName = updateDTO.getServerName().trim();
            updateDTO.setServerName(serverName);
        }

        String ip = oldServer.getIp();
        if (updateDTO.getIp() != null) {
            if (!StringUtils.hasText(updateDTO.getIp())) {
                throw new BusinessException("服务器IP不能为空");
            }
            ip = updateDTO.getIp().trim();
            updateDTO.setIp(ip);
        }

        Integer port = updateDTO.getPort() == null ? oldServer.getPort() : updateDTO.getPort();
        checkDuplicate(serverName, ip, port, updateDTO.getId());

        if (updateDTO.getAccount() != null) {
            if (!StringUtils.hasText(updateDTO.getAccount())) {
                throw new BusinessException("账号不能为空");
            }
            updateDTO.setAccount(updateDTO.getAccount().trim());
        }

        if (updateDTO.getPassword() != null) {
            if (StringUtils.hasText(updateDTO.getPassword())) {
                updateDTO.setPassword(updateDTO.getPassword().trim());
            } else {
                updateDTO.setPassword(null);
            }
        }

        if (updateDTO.getDeployPath() != null) {
            if (!StringUtils.hasText(updateDTO.getDeployPath())) {
                throw new BusinessException("部署地址不能为空");
            }

            String deployPath = DeployPathUtils.normalize(updateDTO.getDeployPath());
            if (!DeployPathUtils.isValidDeployPath(deployPath)) {
                throw new BusinessException("部署地址格式不合法，请填写 Ubuntu/Linux 绝对路径或 Windows 绝对路径");
            }
            updateDTO.setDeployPath(deployPath);
        }

        AlgorithmServer server = new AlgorithmServer();
        BeanUtils.copyProperties(updateDTO, server);
        algorithmServerMapper.updateById(server);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteServer(Long id) {
        AlgorithmServer server = algorithmServerMapper.selectById(id);
        if (server == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法服务器不存在");
        }
        
        Long runtimeCount = algorithmRuntimeMapper.selectCount(
                new LambdaQueryWrapper<AlgorithmRuntime>()
                        .eq(AlgorithmRuntime::getServerId, id)
        );

        if (runtimeCount != null && runtimeCount > 0) {
            throw new BusinessException("该算法服务器下存在已部署算法，不能删除");
        }

        algorithmServerMapper.deleteById(id);
    }

    @Override
    public List<AlgorithmServerOptionVO> listEnabledServerOptions() {
        List<AlgorithmServer> servers = algorithmServerMapper.selectList(
                new LambdaQueryWrapper<AlgorithmServer>()
                        .eq(AlgorithmServer::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByDesc(AlgorithmServer::getCreateTime)
        );

        return servers.stream().map(this::toOptionVO).toList();
    }

    private void checkDuplicate(String serverName, String ip, Integer port, Long excludeId) {
        Long nameCount = algorithmServerMapper.selectCount(new LambdaQueryWrapper<AlgorithmServer>()
                .eq(AlgorithmServer::getServerName, serverName)
                .ne(excludeId != null, AlgorithmServer::getId, excludeId));
        if (nameCount > 0) {
            throw new BusinessException("服务器名已存在");
        }

        Long addressCount = algorithmServerMapper.selectCount(new LambdaQueryWrapper<AlgorithmServer>()
                .eq(AlgorithmServer::getIp, ip)
                .eq(AlgorithmServer::getPort, port)
                .ne(excludeId != null, AlgorithmServer::getId, excludeId));
        if (addressCount > 0) {
            throw new BusinessException("服务器IP和端口已存在");
        }
    }

    private AlgorithmServerVO toVO(AlgorithmServer server) {
        AlgorithmServerVO vo = new AlgorithmServerVO();
        BeanUtils.copyProperties(server, vo);
        return vo;
    }

    private AlgorithmServerOptionVO toOptionVO(AlgorithmServer server) {
        AlgorithmServerOptionVO vo = new AlgorithmServerOptionVO();
        BeanUtils.copyProperties(server, vo);
        return vo;
    }
}