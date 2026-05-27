package com.sk.iba.module.device.service;

import com.sk.iba.module.device.dto.CameraFunctionRoiCreateDTO;
import com.sk.iba.module.device.dto.CameraFunctionRoiUpdateDTO;
import com.sk.iba.module.device.dto.CameraFunctionTimeSaveDTO;
import com.sk.iba.module.device.vo.CameraFunctionRoiVO;
import com.sk.iba.module.device.vo.CameraFunctionTimeVO;

import java.util.List;

/**
 * @author zzy
 */
public interface CameraFunctionConfigService {

    /**
     * 查询ROI列表
     */
    List<CameraFunctionRoiVO> listRois(Long cameraFunctionId);

    /**
     * 新增ROI
     */
    Long createRoi(Long cameraFunctionId, CameraFunctionRoiCreateDTO createDTO);

    /**
     * 修改ROI
     */
    void updateRoi(Long cameraFunctionId, Long roiId, CameraFunctionRoiUpdateDTO updateDTO);

    /**
     * 删除ROI
     */
    void deleteRoi(Long cameraFunctionId, Long roiId);

    /**
     * 查询运行时间段
     */
    List<CameraFunctionTimeVO> listTimes(Long cameraFunctionId);

    /**
     * 保存运行时间段
     */
    void saveTimes(Long cameraFunctionId, CameraFunctionTimeSaveDTO saveDTO);

    /**
     * 清空运行时间段，恢复全天
     */
    void clearTimes(Long cameraFunctionId);
}