package com.sk.iba.module.monitor.service;

import com.sk.iba.module.monitor.dto.ZlmStreamChangedDTO;
import com.sk.iba.module.monitor.vo.CameraLivePlayVO;

/**
 * 实时监控直播 Service
 *
 * @author zzy
 */
public interface MonitorLiveService {

    /**
     * 获取摄像头直播播放地址
     */
    CameraLivePlayVO playCamera(Long cameraId);

    /**
     * ZLM 流状态变化回调
     */
    void handleStreamChanged(ZlmStreamChangedDTO dto);
}