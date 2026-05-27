package com.sk.iba.module.device.service;

import com.sk.iba.module.device.dto.CameraFunctionRoiSaveDTO;
import com.sk.iba.module.device.dto.CameraFunctionTimeSaveDTO;
import com.sk.iba.module.device.vo.CameraFunctionRoiVO;
import com.sk.iba.module.device.vo.CameraFunctionTimeVO;

import java.util.List;

/**
 * @author zzy
 */
public interface CameraFunctionConfigService {

    /**
     * 查询ROI配置
     */
    CameraFunctionRoiVO getRoi(Long cameraFunctionId);

    /**
     * 保存ROI配置
     */
    void saveRoi(Long cameraFunctionId, CameraFunctionRoiSaveDTO saveDTO);

    /**
     * 清空ROI配置，恢复全屏
     */
    void clearRoi(Long cameraFunctionId);

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