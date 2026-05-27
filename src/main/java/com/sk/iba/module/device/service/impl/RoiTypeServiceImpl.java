package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.RoiTypeCreateDTO;
import com.sk.iba.module.device.dto.RoiTypeQueryDTO;
import com.sk.iba.module.device.dto.RoiTypeUpdateDTO;
import com.sk.iba.module.device.entity.CameraFunctionRoi;
import com.sk.iba.module.device.entity.RoiType;
import com.sk.iba.module.device.mapper.CameraFunctionRoiMapper;
import com.sk.iba.module.device.mapper.RoiTypeMapper;
import com.sk.iba.module.device.service.RoiTypeService;
import com.sk.iba.module.device.vo.RoiTypeOptionVO;
import com.sk.iba.module.device.vo.RoiTypeVO;
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
public class RoiTypeServiceImpl implements RoiTypeService {

    private final RoiTypeMapper roiTypeMapper;

    private final CameraFunctionRoiMapper cameraFunctionRoiMapper;

    @Override
    public PageResult<RoiTypeVO> pageRoiTypes(RoiTypeQueryDTO queryDTO) {
        Page<RoiType> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<RoiType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getTypeName()), RoiType::getTypeName, queryDTO.getTypeName())
                .like(StringUtils.hasText(queryDTO.getTypeCode()), RoiType::getTypeCode, queryDTO.getTypeCode())
                .eq(queryDTO.getStatus() != null, RoiType::getStatus, queryDTO.getStatus())
                .orderByDesc(RoiType::getCreateTime);

        Page<RoiType> roiTypePage = roiTypeMapper.selectPage(page, wrapper);
        IPage<RoiTypeVO> voPage = roiTypePage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public RoiTypeVO getRoiTypeById(Long id) {
        RoiType roiType = roiTypeMapper.selectById(id);
        if (roiType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI类型不存在");
        }

        return toVO(roiType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoiType(RoiTypeCreateDTO createDTO) {
        String typeName = createDTO.getTypeName().trim();
        String typeCode = createDTO.getTypeCode().trim();

        Long count = roiTypeMapper.selectCount(new LambdaQueryWrapper<RoiType>()
                .eq(RoiType::getTypeCode, typeCode));

        if (count > 0) {
            throw new BusinessException("ROI类型编码已存在");
        }

        if (createDTO.getStatus() == null) {
            createDTO.setStatus(StatusEnum.ENABLED.getCode());
        }
        if (!StatusEnum.isValid(createDTO.getStatus())) {
            throw new BusinessException("ROI类型状态不合法");
        }

        RoiType roiType = new RoiType();
        BeanUtils.copyProperties(createDTO, roiType);
        roiType.setTypeName(typeName);
        roiType.setTypeCode(typeCode);

        roiTypeMapper.insert(roiType);
        return roiType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoiType(RoiTypeUpdateDTO updateDTO) {
        RoiType oldRoiType = roiTypeMapper.selectById(updateDTO.getId());
        if (oldRoiType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI类型不存在");
        }

        if (updateDTO.getTypeName() != null && !StringUtils.hasText(updateDTO.getTypeName())) {
            throw new BusinessException("ROI类型名称不能为空");
        }

        if (updateDTO.getTypeCode() != null) {
            String typeCode = updateDTO.getTypeCode().trim();

            Long count = roiTypeMapper.selectCount(new LambdaQueryWrapper<RoiType>()
                    .eq(RoiType::getTypeCode, typeCode)
                    .ne(RoiType::getId, updateDTO.getId()));

            if (count > 0) {
                throw new BusinessException("ROI类型编码已存在");
            }

            updateDTO.setTypeCode(typeCode);
        }

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("ROI类型状态不合法");
        }

        RoiType roiType = new RoiType();
        BeanUtils.copyProperties(updateDTO, roiType);

        if (StringUtils.hasText(roiType.getTypeName())) {
            roiType.setTypeName(roiType.getTypeName().trim());
        }

        roiTypeMapper.updateById(roiType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoiType(Long id) {
        RoiType roiType = roiTypeMapper.selectById(id);
        if (roiType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI类型不存在");
        }

        Long count = cameraFunctionRoiMapper.selectCount(new LambdaQueryWrapper<CameraFunctionRoi>()
                .eq(CameraFunctionRoi::getRoiTypeId, id));

        if (count > 0) {
            throw new BusinessException("该ROI类型已被使用，不能删除");
        }

        roiTypeMapper.deleteById(id);
    }

    @Override
    public List<RoiTypeOptionVO> listEnabledRoiTypeOptions() {
        List<RoiType> roiTypes = roiTypeMapper.selectList(
                new LambdaQueryWrapper<RoiType>()
                        .eq(RoiType::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByDesc(RoiType::getCreateTime)
        );

        return roiTypes.stream().map(this::toOptionVO).toList();
    }

    private RoiTypeVO toVO(RoiType roiType) {
        RoiTypeVO vo = new RoiTypeVO();
        BeanUtils.copyProperties(roiType, vo);
        return vo;
    }

    private RoiTypeOptionVO toOptionVO(RoiType roiType) {
        RoiTypeOptionVO vo = new RoiTypeOptionVO();
        BeanUtils.copyProperties(roiType, vo);
        return vo;
    }
}