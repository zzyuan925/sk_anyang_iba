package com.sk.iba.module.device.service;

import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.device.dto.CameraCreateDTO;
import com.sk.iba.module.device.dto.CameraFunctionBatchDTO;
import com.sk.iba.module.device.dto.CameraQueryDTO;
import com.sk.iba.module.device.dto.CameraUpdateDTO;
import com.sk.iba.module.device.vo.CameraFunctionConfigVO;
import com.sk.iba.module.device.vo.CameraOptionVO;
import com.sk.iba.module.device.vo.CameraVO;

import java.util.List;

/**
 * @author zzy
 */
public interface CameraService {

    /**
     * 分页查询摄像头
     */
    PageResult<CameraVO> pageCameras(CameraQueryDTO queryDTO);

    /**
     * 查询摄像头详情
     */
    CameraVO getCameraById(Long id);

    /**
     * 新增摄像头
     */
    Long createCamera(CameraCreateDTO createDTO);

    /**
     * 修改摄像头
     */
    void updateCamera(CameraUpdateDTO updateDTO);

    /**
     * 删除摄像头
     */
    void deleteCamera(Long id);

    /**
     * 查询启用摄像头下拉选项
     */
    List<CameraOptionVO> listEnabledCameraOptions();

    /**
     * 查询摄像头功能配置
     */
    CameraFunctionConfigVO getFunctionConfig(Long cameraId);

    /**
     * 添加摄像头功能
     */
    CameraFunctionConfigVO addFunctions(Long cameraId, CameraFunctionBatchDTO batchDTO);

    /**
     * 移除摄像头功能
     */
    CameraFunctionConfigVO removeFunctions(Long cameraId, CameraFunctionBatchDTO batchDTO);
}