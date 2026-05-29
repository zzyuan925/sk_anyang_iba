package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.constant.AlgorithmRuntimeConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.utils.DeployPathUtils;
import com.sk.iba.module.device.client.AlgorithmRuntimeClient;
import com.sk.iba.module.device.client.AlgorithmSshClient;
import com.sk.iba.module.device.dto.AlgorithmRuntimeDeployDTO;
import com.sk.iba.module.device.dto.AlgorithmRuntimeQueryDTO;
import com.sk.iba.module.device.entity.AlgorithmFunction;
import com.sk.iba.module.device.entity.AlgorithmPackage;
import com.sk.iba.module.device.entity.AlgorithmRuntime;
import com.sk.iba.module.device.entity.AlgorithmServer;
import com.sk.iba.module.device.mapper.AlgorithmFunctionMapper;
import com.sk.iba.module.device.mapper.AlgorithmPackageMapper;
import com.sk.iba.module.device.mapper.AlgorithmRuntimeMapper;
import com.sk.iba.module.device.mapper.AlgorithmServerMapper;
import com.sk.iba.module.device.service.AlgorithmRuntimeService;
import com.sk.iba.module.device.vo.AlgorithmRuntimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 算法运行 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class AlgorithmRuntimeServiceImpl implements AlgorithmRuntimeService {

    private final AlgorithmRuntimeMapper algorithmRuntimeMapper;

    private final AlgorithmServerMapper algorithmServerMapper;

    private final AlgorithmPackageMapper algorithmPackageMapper;

    private final AlgorithmFunctionMapper algorithmFunctionMapper;

    private final AlgorithmSshClient algorithmSshClient;

    private final AlgorithmRuntimeClient algorithmRuntimeClient;

    @Override
    public PageResult<AlgorithmRuntimeVO> pageAlgorithmRuntimes(AlgorithmRuntimeQueryDTO queryDTO) {
        Page<AlgorithmRuntime> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AlgorithmRuntime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getServerId() != null, AlgorithmRuntime::getServerId, queryDTO.getServerId())
                .eq(queryDTO.getFunctionId() != null, AlgorithmRuntime::getFunctionId, queryDTO.getFunctionId())
                .eq(queryDTO.getAlgorithmPackageId() != null, AlgorithmRuntime::getAlgorithmPackageId, queryDTO.getAlgorithmPackageId())
                .eq(queryDTO.getRunStatus() != null, AlgorithmRuntime::getRunStatus, queryDTO.getRunStatus())
                .orderByDesc(AlgorithmRuntime::getCreateTime);

        Page<AlgorithmRuntime> runtimePage = algorithmRuntimeMapper.selectPage(page, wrapper);

        Map<Long, AlgorithmServer> serverMap = buildServerMap(runtimePage.getRecords());
        Map<Long, AlgorithmFunction> functionMap = buildFunctionMap(runtimePage.getRecords());
        Map<Long, AlgorithmPackage> packageMap = buildPackageMap(runtimePage.getRecords());

        IPage<AlgorithmRuntimeVO> voPage = runtimePage.convert(runtime ->
                toVO(runtime, serverMap, functionMap, packageMap)
        );

        return PageResult.of(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long deployAlgorithmPackage(AlgorithmRuntimeDeployDTO deployDTO) {
        AlgorithmServer server = getServer(deployDTO.getServerId());
        checkServerEnabled(server);

        AlgorithmPackage algorithmPackage = getPackage(deployDTO.getAlgorithmPackageId());
        AlgorithmFunction function = getFunction(algorithmPackage.getFunctionId());

        if (!StringUtils.hasText(algorithmPackage.getPackagePath())) {
            throw new BusinessException("算法包文件路径为空");
        }
        if (!StringUtils.hasText(function.getFunctionCode())) {
            throw new BusinessException("算法功能编码为空");
        }
        if (!StringUtils.hasText(algorithmPackage.getVersion())) {
            throw new BusinessException("算法包版本为空");
        }

        Long existsCount = algorithmRuntimeMapper.selectCount(
                new LambdaQueryWrapper<AlgorithmRuntime>()
                        .eq(AlgorithmRuntime::getServerId, server.getId())
                        .eq(AlgorithmRuntime::getAlgorithmPackageId, algorithmPackage.getId())
        );

        if (existsCount != null && existsCount > 0) {
            throw new BusinessException("该算法包已部署到该服务器");
        }

        String deployPath = buildDeployPath(server, function, algorithmPackage);

        algorithmSshClient.uploadAndUnzip(server, algorithmPackage.getPackagePath(), deployPath);

        try {
            AlgorithmRuntime runtime = new AlgorithmRuntime();
            runtime.setServerId(server.getId());
            runtime.setFunctionId(function.getId());
            runtime.setAlgorithmPackageId(algorithmPackage.getId());
            runtime.setDeployPath(deployPath);
            runtime.setRunStatus(AlgorithmRuntimeConstants.RUN_STATUS_STOPPED);

            algorithmRuntimeMapper.insert(runtime);
            return runtime.getId();
        } catch (DuplicateKeyException e) {
            throw new BusinessException("该算法包已部署到该服务器");
        }
    }

    @Override
    public void startAlgorithm(Long id) {
        AlgorithmRuntime runtime = getRuntime(id);

        if (Objects.equals(runtime.getRunStatus(), AlgorithmRuntimeConstants.RUN_STATUS_RUNNING)) {
            return;
        }

        AlgorithmServer server = getServer(runtime.getServerId());
        checkServerEnabled(server);

        AlgorithmPackage algorithmPackage = getPackage(runtime.getAlgorithmPackageId());
        AlgorithmFunction function = getFunction(runtime.getFunctionId());

        Long runningCount = algorithmRuntimeMapper.selectCount(
                new LambdaQueryWrapper<AlgorithmRuntime>()
                        .eq(AlgorithmRuntime::getServerId, runtime.getServerId())
                        .eq(AlgorithmRuntime::getFunctionId, runtime.getFunctionId())
                        .eq(AlgorithmRuntime::getRunStatus, AlgorithmRuntimeConstants.RUN_STATUS_RUNNING)
                        .ne(AlgorithmRuntime::getId, runtime.getId())
        );

        if (runningCount != null && runningCount > 0) {
            throw new BusinessException("该服务器上此算法功能已有运行中的版本，请先停止");
        }

        try {
            algorithmRuntimeClient.start(
                    server,
                    runtime.getDeployPath(),
                    function.getFunctionCode(),
                    algorithmPackage.getRuntimeEnv(),
                    algorithmPackage.getStartFileName(),
                    algorithmPackage.getWeightPath()
            );

            updateRunStatus(runtime.getId(), AlgorithmRuntimeConstants.RUN_STATUS_RUNNING);
        } catch (Exception e) {
            updateRunStatus(runtime.getId(), AlgorithmRuntimeConstants.RUN_STATUS_ERROR);
            throw e;
        }
    }

    @Override
    public void stopAlgorithm(Long id) {
        AlgorithmRuntime runtime = getRuntime(id);

        if (Objects.equals(runtime.getRunStatus(), AlgorithmRuntimeConstants.RUN_STATUS_STOPPED)) {
            return;
        }

        AlgorithmServer server = getServer(runtime.getServerId());
        checkServerEnabled(server);

        AlgorithmPackage algorithmPackage = getPackage(runtime.getAlgorithmPackageId());
        AlgorithmFunction function = getFunction(runtime.getFunctionId());

        algorithmRuntimeClient.stop(
                server,
                runtime.getDeployPath(),
                function.getFunctionCode(),
                algorithmPackage.getRuntimeEnv(),
                algorithmPackage.getStartFileName(),
                algorithmPackage.getWeightPath()
        );

        updateRunStatus(runtime.getId(), AlgorithmRuntimeConstants.RUN_STATUS_STOPPED);
    }

    private void updateRunStatus(Long id, Integer runStatus) {
        AlgorithmRuntime update = new AlgorithmRuntime();
        update.setId(id);
        update.setRunStatus(runStatus);
        algorithmRuntimeMapper.updateById(update);
    }

    private AlgorithmRuntime getRuntime(Long id) {
        AlgorithmRuntime runtime = algorithmRuntimeMapper.selectById(id);
        if (runtime == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法运行记录不存在");
        }
        return runtime;
    }

    private AlgorithmServer getServer(Long id) {
        AlgorithmServer server = algorithmServerMapper.selectById(id);
        if (server == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法服务器不存在");
        }
        return server;
    }

    private AlgorithmPackage getPackage(Long id) {
        AlgorithmPackage algorithmPackage = algorithmPackageMapper.selectById(id);
        if (algorithmPackage == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法包不存在");
        }
        return algorithmPackage;
    }

    private AlgorithmFunction getFunction(Long id) {
        AlgorithmFunction function = algorithmFunctionMapper.selectById(id);
        if (function == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法功能不存在");
        }
        return function;
    }

    private void checkServerEnabled(AlgorithmServer server) {
        if (!Objects.equals(server.getStatus(), StatusEnum.ENABLED.getCode())) {
            throw new BusinessException("算法服务器未启用");
        }
    }

    private String buildDeployPath(AlgorithmServer server,
                                   AlgorithmFunction function,
                                   AlgorithmPackage algorithmPackage) {
        String basePath = DeployPathUtils.normalize(server.getDeployPath());
        if (!DeployPathUtils.isValidDeployPath(basePath)) {
            throw new BusinessException("算法服务器部署路径不合法");
        }

        String functionCode = safePathPart(function.getFunctionCode());
        String version = safePathPart(algorithmPackage.getVersion());

        return basePath + "/" + functionCode + "/" + version;
    }

    private String safePathPart(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("路径参数不能为空");
        }

        String text = value.trim();
        if (text.contains("..") || text.contains("/") || text.contains("\\")) {
            throw new BusinessException("路径参数不合法：" + value);
        }

        return text;
    }

    private Map<Long, AlgorithmServer> buildServerMap(List<AlgorithmRuntime> runtimes) {
        Set<Long> ids = runtimes.stream()
                .map(AlgorithmRuntime::getServerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return algorithmServerMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(AlgorithmServer::getId, item -> item));
    }

    private Map<Long, AlgorithmFunction> buildFunctionMap(List<AlgorithmRuntime> runtimes) {
        Set<Long> ids = runtimes.stream()
                .map(AlgorithmRuntime::getFunctionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return algorithmFunctionMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(AlgorithmFunction::getId, item -> item));
    }

    private Map<Long, AlgorithmPackage> buildPackageMap(List<AlgorithmRuntime> runtimes) {
        Set<Long> ids = runtimes.stream()
                .map(AlgorithmRuntime::getAlgorithmPackageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        return algorithmPackageMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(AlgorithmPackage::getId, item -> item));
    }

    private AlgorithmRuntimeVO toVO(AlgorithmRuntime runtime,
                                    Map<Long, AlgorithmServer> serverMap,
                                    Map<Long, AlgorithmFunction> functionMap,
                                    Map<Long, AlgorithmPackage> packageMap) {
        AlgorithmRuntimeVO vo = new AlgorithmRuntimeVO();
        vo.setId(runtime.getId());
        vo.setServerId(runtime.getServerId());
        vo.setFunctionId(runtime.getFunctionId());
        vo.setAlgorithmPackageId(runtime.getAlgorithmPackageId());
        vo.setDeployPath(runtime.getDeployPath());
        vo.setRunStatus(runtime.getRunStatus());
        vo.setCreateTime(runtime.getCreateTime());
        vo.setUpdateTime(runtime.getUpdateTime());

        AlgorithmServer server = serverMap.get(runtime.getServerId());
        if (server != null) {
            vo.setServerName(server.getServerName());
            vo.setServerIp(server.getIp());
        }

        AlgorithmFunction function = functionMap.get(runtime.getFunctionId());
        if (function != null) {
            vo.setFunctionName(function.getFunctionName());
            vo.setFunctionCode(function.getFunctionCode());
        }

        AlgorithmPackage algorithmPackage = packageMap.get(runtime.getAlgorithmPackageId());
        if (algorithmPackage != null) {
            vo.setPackageVersion(algorithmPackage.getVersion());
            vo.setPackagePath(algorithmPackage.getPackagePath());
            vo.setRuntimeEnv(algorithmPackage.getRuntimeEnv());
            vo.setStartFileName(algorithmPackage.getStartFileName());
            vo.setWeightPath(algorithmPackage.getWeightPath());
        }

        return vo;
    }
}