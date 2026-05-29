package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.common.utils.FileUploadUtil;
import com.sk.iba.module.device.dto.AlgorithmPackageCreateDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageQueryDTO;
import com.sk.iba.module.device.dto.AlgorithmPackageUpdateDTO;
import com.sk.iba.module.device.entity.AlgorithmFunction;
import com.sk.iba.module.device.entity.AlgorithmPackage;
import com.sk.iba.module.device.mapper.AlgorithmFunctionMapper;
import com.sk.iba.module.device.mapper.AlgorithmPackageMapper;
import com.sk.iba.module.device.service.AlgorithmPackageService;
import com.sk.iba.module.device.vo.AlgorithmPackageOptionVO;
import com.sk.iba.module.device.vo.AlgorithmPackageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 算法包 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class AlgorithmPackageServiceImpl implements AlgorithmPackageService {

    private final AlgorithmPackageMapper algorithmPackageMapper;

    private final AlgorithmFunctionMapper algorithmFunctionMapper;

    private final FileUploadUtil fileUploadUtil;

    @Override
    public PageResult<AlgorithmPackageVO> pageAlgorithmPackages(AlgorithmPackageQueryDTO queryDTO) {
        Page<AlgorithmPackage> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AlgorithmPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getFunctionId() != null, AlgorithmPackage::getFunctionId, queryDTO.getFunctionId())
                .like(StringUtils.hasText(queryDTO.getVersion()), AlgorithmPackage::getVersion, queryDTO.getVersion())
                .like(StringUtils.hasText(queryDTO.getRuntimeEnv()), AlgorithmPackage::getRuntimeEnv, queryDTO.getRuntimeEnv())
                .orderByDesc(AlgorithmPackage::getCreateTime);

        Page<AlgorithmPackage> packagePage = algorithmPackageMapper.selectPage(page, wrapper);

        Map<Long, AlgorithmFunction> functionMap = buildFunctionMap(packagePage.getRecords());
        IPage<AlgorithmPackageVO> voPage = packagePage.convert(algorithmPackage -> toVO(algorithmPackage, functionMap));

        return PageResult.of(voPage);
    }

    @Override
    public AlgorithmPackageVO getAlgorithmPackageById(Long id) {
        AlgorithmPackage algorithmPackage = getAlgorithmPackage(id);
        Map<Long, AlgorithmFunction> functionMap = buildFunctionMap(List.of(algorithmPackage));
        return toVO(algorithmPackage, functionMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAlgorithmPackage(AlgorithmPackageCreateDTO createDTO) {
        AlgorithmFunction function = algorithmFunctionMapper.selectById(createDTO.getFunctionId());
        if (function == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法功能不存在");
        }
        if (!StringUtils.hasText(function.getFunctionCode())) {
            throw new BusinessException("算法功能代号不能为空");
        }

        String version = createDTO.getVersion().trim();
        checkVersionNotExists(createDTO.getFunctionId(), version);

        String packagePath = null;
        try {
            packagePath = fileUploadUtil.uploadAlgorithmPackage(
                    createDTO.getFile(),
                    function.getFunctionCode(),
                    version
            );

            AlgorithmPackage algorithmPackage = new AlgorithmPackage();
            algorithmPackage.setFunctionId(createDTO.getFunctionId());
            algorithmPackage.setPackagePath(packagePath);
            algorithmPackage.setDescription(trimToNull(createDTO.getDescription()));
            algorithmPackage.setVersion(version);
            algorithmPackage.setRuntimeEnv(trimToNull(createDTO.getRuntimeEnv()));
            algorithmPackage.setStartFileName(createDTO.getStartFileName().trim());
            algorithmPackage.setWeightPath(trimToNull(createDTO.getWeightPath()));

            algorithmPackageMapper.insert(algorithmPackage);
            return algorithmPackage.getId();
        } catch (DuplicateKeyException e) {
            fileUploadUtil.deleteQuietly(packagePath);
            throw new BusinessException("该算法功能下已存在相同版本的算法包");
        } catch (BusinessException e) {
            fileUploadUtil.deleteQuietly(packagePath);
            throw e;
        } catch (Exception e) {
            fileUploadUtil.deleteQuietly(packagePath);
            throw new BusinessException("上传算法包失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlgorithmPackage(AlgorithmPackageUpdateDTO updateDTO) {
        AlgorithmPackage oldPackage = getAlgorithmPackage(updateDTO.getId());

        AlgorithmPackage updatePackage = new AlgorithmPackage();
        updatePackage.setId(oldPackage.getId());
        updatePackage.setDescription(trimToNull(updateDTO.getDescription()));
        updatePackage.setRuntimeEnv(trimToNull(updateDTO.getRuntimeEnv()));
        updatePackage.setStartFileName(trimToNull(updateDTO.getStartFileName()));
        updatePackage.setWeightPath(trimToNull(updateDTO.getWeightPath()));

        algorithmPackageMapper.updateById(updatePackage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlgorithmPackage(Long id) {
        AlgorithmPackage algorithmPackage = getAlgorithmPackage(id);
        algorithmPackageMapper.deleteById(algorithmPackage.getId());
    }

    @Override
    public List<AlgorithmPackageOptionVO> listAlgorithmPackageOptions(Long functionId) {
        List<AlgorithmPackage> algorithmPackages = algorithmPackageMapper.selectList(
                new LambdaQueryWrapper<AlgorithmPackage>()
                        .eq(functionId != null, AlgorithmPackage::getFunctionId, functionId)
                        .orderByDesc(AlgorithmPackage::getCreateTime)
        );

        if (algorithmPackages.isEmpty()) {
            return List.of();
        }

        Map<Long, AlgorithmFunction> functionMap = buildFunctionMap(algorithmPackages);

        return algorithmPackages.stream()
                .map(algorithmPackage -> toOptionVO(algorithmPackage, functionMap))
                .toList();
    }

    private AlgorithmPackageOptionVO toOptionVO(AlgorithmPackage algorithmPackage,
                                                Map<Long, AlgorithmFunction> functionMap) {
        AlgorithmPackageOptionVO vo = new AlgorithmPackageOptionVO();
        vo.setId(algorithmPackage.getId());
        vo.setFunctionId(algorithmPackage.getFunctionId());
        vo.setVersion(algorithmPackage.getVersion());
        vo.setRuntimeEnv(algorithmPackage.getRuntimeEnv());

        AlgorithmFunction function = functionMap.get(algorithmPackage.getFunctionId());
        if (function != null) {
            vo.setFunctionName(function.getFunctionName());
            vo.setFunctionCode(function.getFunctionCode());

            String name = StringUtils.hasText(function.getFunctionName())
                    ? function.getFunctionName()
                    : function.getFunctionCode();

            vo.setLabel(name + " - " + algorithmPackage.getVersion());
        } else {
            vo.setLabel(algorithmPackage.getVersion());
        }

        return vo;
    }

    private AlgorithmPackage getAlgorithmPackage(Long id) {
        AlgorithmPackage algorithmPackage = algorithmPackageMapper.selectById(id);
        if (algorithmPackage == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法包不存在");
        }
        return algorithmPackage;
    }

    private void checkVersionNotExists(Long functionId, String version) {
        Long count = algorithmPackageMapper.selectCount(
                new LambdaQueryWrapper<AlgorithmPackage>()
                        .eq(AlgorithmPackage::getFunctionId, functionId)
                        .eq(AlgorithmPackage::getVersion, version)
        );

        if (count != null && count > 0) {
            throw new BusinessException("该算法功能下已存在相同版本的算法包");
        }
    }

    private Map<Long, AlgorithmFunction> buildFunctionMap(List<AlgorithmPackage> algorithmPackages) {
        Set<Long> functionIds = algorithmPackages.stream()
                .map(AlgorithmPackage::getFunctionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (functionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AlgorithmFunction> functions = algorithmFunctionMapper.selectBatchIds(functionIds);
        return functions.stream()
                .collect(Collectors.toMap(AlgorithmFunction::getId, function -> function));
    }

    private AlgorithmPackageVO toVO(AlgorithmPackage algorithmPackage, Map<Long, AlgorithmFunction> functionMap) {
        AlgorithmPackageVO vo = new AlgorithmPackageVO();
        vo.setId(algorithmPackage.getId());
        vo.setFunctionId(algorithmPackage.getFunctionId());
        vo.setPackagePath(algorithmPackage.getPackagePath());
        vo.setDescription(algorithmPackage.getDescription());
        vo.setVersion(algorithmPackage.getVersion());
        vo.setRuntimeEnv(algorithmPackage.getRuntimeEnv());
        vo.setStartFileName(algorithmPackage.getStartFileName());
        vo.setWeightPath(algorithmPackage.getWeightPath());
        vo.setCreateBy(algorithmPackage.getCreateBy());
        vo.setCreateTime(algorithmPackage.getCreateTime());
        vo.setUpdateBy(algorithmPackage.getUpdateBy());
        vo.setUpdateTime(algorithmPackage.getUpdateTime());

        AlgorithmFunction function = functionMap.get(algorithmPackage.getFunctionId());
        if (function != null) {
            vo.setFunctionName(function.getFunctionName());
            vo.setFunctionCode(function.getFunctionCode());
        }

        return vo;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}