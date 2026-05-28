package com.sk.iba.module.device.service;

import com.sk.iba.module.device.dto.CameraDirectProbeDTO;
import com.sk.iba.module.device.vo.CameraDirectProbeVO;
import com.sk.iba.module.device.vo.PlatformCameraVO;

import java.util.List;

/**
 * 摄像头接入 Service
 *
 * @author zzy
 */
public interface CameraAccessService {

    /**
     * 直连摄像头识别
     */
    CameraDirectProbeVO directProbe(CameraDirectProbeDTO probeDTO);

    /**
     * 平台摄像头搜索
     */
    List<PlatformCameraVO> searchPlatformCamera(String cameraName, Integer pageNo, Integer pageSize);

    /**
     * 获取平台摄像头预览地址
     */
    String getPlatformPreviewUrl(String cameraIndexCode);
}