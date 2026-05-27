package com.sk.iba.module.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.dto.CameraFunctionRoiSaveDTO;
import com.sk.iba.module.device.dto.CameraFunctionTimeSaveDTO;
import com.sk.iba.module.device.dto.TimePeriodDTO;
import com.sk.iba.module.device.entity.CameraFunction;
import com.sk.iba.module.device.entity.CameraFunctionRoi;
import com.sk.iba.module.device.entity.CameraFunctionTime;
import com.sk.iba.module.device.mapper.CameraFunctionMapper;
import com.sk.iba.module.device.mapper.CameraFunctionRoiMapper;
import com.sk.iba.module.device.mapper.CameraFunctionTimeMapper;
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

/**
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class CameraFunctionConfigServiceImpl implements CameraFunctionConfigService {

    private final CameraFunctionMapper cameraFunctionMapper;

    private final CameraFunctionRoiMapper cameraFunctionRoiMapper;

    private final CameraFunctionTimeMapper cameraFunctionTimeMapper;

    private final CameraService cameraService;

    @Override
    public CameraFunctionRoiVO getRoi(Long cameraFunctionId) {
        checkCameraFunctionPermission(cameraFunctionId);

        CameraFunctionRoi roi = cameraFunctionRoiMapper.selectOne(
                new LambdaQueryWrapper<CameraFunctionRoi>()
                        .eq(CameraFunctionRoi::getCameraFunctionId, cameraFunctionId)
                        .last("LIMIT 1")
        );

        CameraFunctionRoiVO vo = new CameraFunctionRoiVO();
        vo.setCameraFunctionId(cameraFunctionId);

        if (roi == null) {
            vo.setRoiConfigured(0);
            vo.setRoiText("全屏");
            return vo;
        }

        vo.setRoiData(roi.getRoiData());
        vo.setRoiConfigured(1);
        vo.setRoiText("已配置");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoi(Long cameraFunctionId, CameraFunctionRoiSaveDTO saveDTO) {
        checkCameraFunctionPermission(cameraFunctionId);

        String roiData = saveDTO.getRoiData().trim();
        if (!StringUtils.hasText(roiData)) {
            throw new BusinessException("ROI坐标不能为空");
        }

        cameraFunctionRoiMapper.delete(new LambdaQueryWrapper<CameraFunctionRoi>()
                .eq(CameraFunctionRoi::getCameraFunctionId, cameraFunctionId));

        CameraFunctionRoi roi = new CameraFunctionRoi();
        roi.setCameraFunctionId(cameraFunctionId);
        roi.setRoiData(roiData);

        cameraFunctionRoiMapper.insert(roi);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearRoi(Long cameraFunctionId) {
        checkCameraFunctionPermission(cameraFunctionId);

        cameraFunctionRoiMapper.delete(new LambdaQueryWrapper<CameraFunctionRoi>()
                .eq(CameraFunctionRoi::getCameraFunctionId, cameraFunctionId));
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

        // 复用摄像头详情权限校验：无权访问该摄像头时这里会抛异常
        cameraService.getCameraById(cameraFunction.getCameraId());

        return cameraFunction;
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

    private CameraFunctionTimeVO toTimeVO(CameraFunctionTime time) {
        CameraFunctionTimeVO vo = new CameraFunctionTimeVO();
        BeanUtils.copyProperties(time, vo);
        return vo;
    }
}