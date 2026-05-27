package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.FunctionCreateDTO;
import com.sk.iba.module.device.dto.FunctionQueryDTO;
import com.sk.iba.module.device.dto.FunctionUpdateDTO;
import com.sk.iba.module.device.entity.AlgorithmFunction;
import com.sk.iba.module.device.entity.CameraFunction;
import com.sk.iba.module.device.mapper.AlgorithmFunctionMapper;
import com.sk.iba.module.device.mapper.CameraFunctionMapper;
import com.sk.iba.module.device.service.AlgorithmFunctionService;
import com.sk.iba.module.device.vo.FunctionOptionVO;
import com.sk.iba.module.device.vo.FunctionVO;
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
public class AlgorithmFunctionServiceImpl implements AlgorithmFunctionService {

    private final AlgorithmFunctionMapper algorithmFunctionMapper;

    private final CameraFunctionMapper cameraFunctionMapper;

    @Override
    public PageResult<FunctionVO> pageFunctions(FunctionQueryDTO queryDTO) {
        Page<AlgorithmFunction> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<AlgorithmFunction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getFunctionName()), AlgorithmFunction::getFunctionName, queryDTO.getFunctionName())
                .like(StringUtils.hasText(queryDTO.getFunctionCode()), AlgorithmFunction::getFunctionCode, queryDTO.getFunctionCode())
                .eq(StringUtils.hasText(queryDTO.getFunctionType()), AlgorithmFunction::getFunctionType, queryDTO.getFunctionType())
                .eq(queryDTO.getStatus() != null, AlgorithmFunction::getStatus, queryDTO.getStatus())
                .orderByDesc(AlgorithmFunction::getCreateTime);

        Page<AlgorithmFunction> functionPage = algorithmFunctionMapper.selectPage(page, wrapper);
        IPage<FunctionVO> voPage = functionPage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public FunctionVO getFunctionById(Long id) {
        AlgorithmFunction function = algorithmFunctionMapper.selectById(id);
        if (function == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法功能不存在");
        }

        return toVO(function);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFunction(FunctionCreateDTO createDTO) {
        String functionName = createDTO.getFunctionName().trim();
        String functionCode = createDTO.getFunctionCode().trim();

        Long count = algorithmFunctionMapper.selectCount(new LambdaQueryWrapper<AlgorithmFunction>()
                .eq(AlgorithmFunction::getFunctionCode, functionCode));

        if (count > 0) {
            throw new BusinessException("功能编码已存在");
        }

        if (createDTO.getStatus() == null) {
            createDTO.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(createDTO.getStatus())) {
            throw new BusinessException("功能状态不合法");
        }

        AlgorithmFunction function = new AlgorithmFunction();
        BeanUtils.copyProperties(createDTO, function);
        function.setFunctionName(functionName);
        function.setFunctionCode(functionCode);

        if (StringUtils.hasText(function.getFunctionType())) {
            function.setFunctionType(function.getFunctionType().trim());
        }

        algorithmFunctionMapper.insert(function);
        return function.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFunction(FunctionUpdateDTO updateDTO) {
        AlgorithmFunction oldFunction = algorithmFunctionMapper.selectById(updateDTO.getId());
        if (oldFunction == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法功能不存在");
        }

        if (updateDTO.getFunctionName() != null && !StringUtils.hasText(updateDTO.getFunctionName())) {
            throw new BusinessException("功能名称不能为空");
        }

        if (updateDTO.getFunctionCode() != null) {
            String functionCode = updateDTO.getFunctionCode().trim();

            Long count = algorithmFunctionMapper.selectCount(new LambdaQueryWrapper<AlgorithmFunction>()
                    .eq(AlgorithmFunction::getFunctionCode, functionCode)
                    .ne(AlgorithmFunction::getId, updateDTO.getId()));

            if (count > 0) {
                throw new BusinessException("功能编码已存在");
            }

            updateDTO.setFunctionCode(functionCode);
        }

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("功能状态不合法");
        }

        AlgorithmFunction function = new AlgorithmFunction();
        BeanUtils.copyProperties(updateDTO, function);

        if (StringUtils.hasText(function.getFunctionName())) {
            function.setFunctionName(function.getFunctionName().trim());
        }
        if (StringUtils.hasText(function.getFunctionType())) {
            function.setFunctionType(function.getFunctionType().trim());
        }

        algorithmFunctionMapper.updateById(function);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFunction(Long id) {
        AlgorithmFunction function = algorithmFunctionMapper.selectById(id);
        if (function == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "算法功能不存在");
        }

        Long count = cameraFunctionMapper.selectCount(new LambdaQueryWrapper<CameraFunction>()
                .eq(CameraFunction::getFunctionId, id));

        if (count > 0) {
            throw new BusinessException("该功能已绑定摄像头，不能删除");
        }

        algorithmFunctionMapper.deleteById(id);
    }

    @Override
    public List<FunctionOptionVO> listEnabledFunctionOptions() {
        List<AlgorithmFunction> functions = algorithmFunctionMapper.selectList(
                new LambdaQueryWrapper<AlgorithmFunction>()
                        .eq(AlgorithmFunction::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByDesc(AlgorithmFunction::getCreateTime)
        );

        return functions.stream().map(this::toOptionVO).toList();
    }

    private FunctionVO toVO(AlgorithmFunction function) {
        FunctionVO vo = new FunctionVO();
        BeanUtils.copyProperties(function, vo);
        return vo;
    }

    private FunctionOptionVO toOptionVO(AlgorithmFunction function) {
        FunctionOptionVO vo = new FunctionOptionVO();
        BeanUtils.copyProperties(function, vo);
        return vo;
    }
}