package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.dto.CameraFunctionRoiCreateDTO;
import com.sk.iba.module.device.dto.CameraFunctionRoiUpdateDTO;
import com.sk.iba.module.device.dto.CameraFunctionTimeSaveDTO;
import com.sk.iba.module.device.dto.TimePeriodDTO;
import com.sk.iba.module.device.entity.CameraFunction;
import com.sk.iba.module.device.entity.CameraFunctionRoi;
import com.sk.iba.module.device.entity.CameraFunctionTime;
import com.sk.iba.module.device.entity.RoiType;
import com.sk.iba.module.device.mapper.CameraFunctionMapper;
import com.sk.iba.module.device.mapper.CameraFunctionRoiMapper;
import com.sk.iba.module.device.mapper.CameraFunctionTimeMapper;
import com.sk.iba.module.device.mapper.RoiTypeMapper;
import com.sk.iba.module.device.service.CameraFunctionConfigService;
import com.sk.iba.module.device.service.CameraService;
import com.sk.iba.module.device.vo.CameraFunctionRoiVO;
import com.sk.iba.module.device.vo.CameraFunctionTimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class CameraFunctionConfigServiceImpl implements CameraFunctionConfigService {

    private final CameraFunctionMapper cameraFunctionMapper;

    private final CameraFunctionRoiMapper cameraFunctionRoiMapper;

    private final CameraFunctionTimeMapper cameraFunctionTimeMapper;

    private final RoiTypeMapper roiTypeMapper;

    private final CameraService cameraService;

    @Override
    public List<CameraFunctionRoiVO> listRois(Long cameraFunctionId) {
        checkCameraFunctionPermission(cameraFunctionId);

        List<CameraFunctionRoi> rois = cameraFunctionRoiMapper.selectList(
                new LambdaQueryWrapper<CameraFunctionRoi>()
                        .eq(CameraFunctionRoi::getCameraFunctionId, cameraFunctionId)
                        .orderByAsc(CameraFunctionRoi::getId)
        );

        if (rois.isEmpty()) {
            return List.of();
        }

        List<Long> roiTypeIds = rois.stream()
                .map(CameraFunctionRoi::getRoiTypeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, RoiType> roiTypeMap = roiTypeIds.isEmpty()
                ? Map.of()
                : roiTypeMapper.selectBatchIds(roiTypeIds).stream()
                .collect(Collectors.toMap(RoiType::getId, item -> item));

        return rois.stream()
                .map(roi -> toRoiVO(roi, roiTypeMap.get(roi.getRoiTypeId())))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoi(Long cameraFunctionId, CameraFunctionRoiCreateDTO createDTO) {
        checkCameraFunctionPermission(cameraFunctionId);

        checkRoiType(createDTO.getRoiTypeId());

        String roiData = createDTO.getRoiData().trim();
        if (!StringUtils.hasText(roiData)) {
            throw new BusinessException("ROI坐标不能为空");
        }

        CameraFunctionRoi roi = new CameraFunctionRoi();
        roi.setCameraFunctionId(cameraFunctionId);
        roi.setRoiName(StringUtils.hasText(createDTO.getRoiName()) ? createDTO.getRoiName().trim() : null);
        roi.setRoiTypeId(createDTO.getRoiTypeId());
        roi.setRoiData(roiData);

        cameraFunctionRoiMapper.insert(roi);
        return roi.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoi(Long cameraFunctionId, Long roiId, CameraFunctionRoiUpdateDTO updateDTO) {
        checkCameraFunctionPermission(cameraFunctionId);

        CameraFunctionRoi oldRoi = cameraFunctionRoiMapper.selectById(roiId);
        if (oldRoi == null || !cameraFunctionId.equals(oldRoi.getCameraFunctionId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI不存在");
        }

        checkRoiType(updateDTO.getRoiTypeId());

        String roiData = updateDTO.getRoiData().trim();
        if (!StringUtils.hasText(roiData)) {
            throw new BusinessException("ROI坐标不能为空");
        }

        CameraFunctionRoi roi = new CameraFunctionRoi();
        roi.setId(roiId);
        roi.setRoiName(StringUtils.hasText(updateDTO.getRoiName()) ? updateDTO.getRoiName().trim() : null);
        roi.setRoiTypeId(updateDTO.getRoiTypeId());
        roi.setRoiData(roiData);

        cameraFunctionRoiMapper.updateById(roi);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoi(Long cameraFunctionId, Long roiId) {
        checkCameraFunctionPermission(cameraFunctionId);

        CameraFunctionRoi roi = cameraFunctionRoiMapper.selectById(roiId);
        if (roi == null || !cameraFunctionId.equals(roi.getCameraFunctionId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI不存在");
        }

        cameraFunctionRoiMapper.deleteById(roiId);
    }

    @Override
    public List<CameraFunctionTimeVO> listTimes(Long cameraFunctionId) {
        checkCameraFunctionPermission(cameraFunctionId);

        List<CameraFunctionTime> times = cameraFunctionTimeMapper.selectList(
                new LambdaQueryWrapper<CameraFunctionTime>()
                        .eq(CameraFunctionTime::getCameraFunctionId, cameraFunctionId)
                        .orderByAsc(CameraFunctionTime::getStartTime)
                        .orderByAsc(CameraFunctionTime::getEndTime)
        );

        return times.stream().map(this::toTimeVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTimes(Long cameraFunctionId, CameraFunctionTimeSaveDTO saveDTO) {
        checkCameraFunctionPermission(cameraFunctionId);

        cameraFunctionTimeMapper.delete(new LambdaQueryWrapper<CameraFunctionTime>()
                .eq(CameraFunctionTime::getCameraFunctionId, cameraFunctionId));

        if (saveDTO.getTimes() == null || saveDTO.getTimes().isEmpty()) {
            return;
        }

        List<CameraFunctionTime> times = saveDTO.getTimes().stream()
                .map(item -> toTime(cameraFunctionId, item))
                .toList();

        cameraFunctionTimeMapper.insertBatch(times);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearTimes(Long cameraFunctionId) {
        checkCameraFunctionPermission(cameraFunctionId);

        cameraFunctionTimeMapper.delete(new LambdaQueryWrapper<CameraFunctionTime>()
                .eq(CameraFunctionTime::getCameraFunctionId, cameraFunctionId));
    }

    private CameraFunction checkCameraFunctionPermission(Long cameraFunctionId) {
        CameraFunction cameraFunction = cameraFunctionMapper.selectById(cameraFunctionId);
        if (cameraFunction == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "摄像头功能不存在");
        }

        cameraService.getCameraById(cameraFunction.getCameraId());
        return cameraFunction;
    }

    private void checkRoiType(Long roiTypeId) {
        RoiType roiType = roiTypeMapper.selectById(roiTypeId);
        if (roiType == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "ROI类型不存在");
        }

        if (!StatusEnum.isEnabled(roiType.getStatus())) {
            throw new BusinessException("不能选择已禁用ROI类型");
        }
    }

    private CameraFunctionTime toTime(Long cameraFunctionId, TimePeriodDTO dto) {
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = dto.getEndTime();

        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("开始时间必须早于结束时间");
        }

        CameraFunctionTime time = new CameraFunctionTime();
        time.setCameraFunctionId(cameraFunctionId);
        time.setStartTime(startTime);
        time.setEndTime(endTime);
        return time;
    }

    private CameraFunctionRoiVO toRoiVO(CameraFunctionRoi roi, RoiType roiType) {
        CameraFunctionRoiVO vo = new CameraFunctionRoiVO();
        BeanUtils.copyProperties(roi, vo);

        if (roiType != null) {
            vo.setRoiTypeName(roiType.getTypeName());
            vo.setRoiTypeCode(roiType.getTypeCode());
        }

        return vo;
    }

    private CameraFunctionTimeVO toTimeVO(CameraFunctionTime time) {
        CameraFunctionTimeVO vo = new CameraFunctionTimeVO();
        BeanUtils.copyProperties(time, vo);
        return vo;
    }
}