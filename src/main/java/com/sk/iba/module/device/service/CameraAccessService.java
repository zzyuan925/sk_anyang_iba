package com.sk.iba.module.device.service;

import com.sk.iba.module.device.dto.CameraDirectProbeDTO;
import com.sk.iba.module.device.vo.CameraDirectProbeVO;

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
}