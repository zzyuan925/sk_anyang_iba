package com.sk.iba.module.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sk.iba.common.constant.LiveStreamConstants;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.service.CameraService;
import com.sk.iba.module.device.vo.CameraVO;
import com.sk.iba.module.monitor.client.ZlmClient;
import com.sk.iba.module.monitor.dto.ZlmStreamChangedDTO;
import com.sk.iba.module.monitor.entity.CameraLiveStream;
import com.sk.iba.module.monitor.entity.MediaServer;
import com.sk.iba.module.monitor.mapper.CameraLiveStreamMapper;
import com.sk.iba.module.monitor.mapper.MediaServerMapper;
import com.sk.iba.module.monitor.service.MonitorLiveService;
import com.sk.iba.module.monitor.vo.CameraLivePlayVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 实时监控直播 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class MonitorLiveServiceImpl implements MonitorLiveService {

    /**
     * 摄像头视频源类型：RTSP
     */
    private static final Integer SOURCE_TYPE_RTSP = 1;

    private final CameraService cameraService;

    private final MediaServerMapper mediaServerMapper;

    private final CameraLiveStreamMapper cameraLiveStreamMapper;

    private final ZlmClient zlmClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CameraLivePlayVO playCamera(Long cameraId) {
        CameraVO camera = cameraService.getCameraById(cameraId);

        if (!StatusEnum.isEnabled(camera.getStatus())) {
            throw new BusinessException("摄像头已禁用");
        }

        if (!SOURCE_TYPE_RTSP.equals(camera.getSourceType())) {
            throw new BusinessException("当前仅支持 RTSP 摄像头直播");
        }

        if (!StringUtils.hasText(camera.getSourceUrl())) {
            throw new BusinessException("摄像头视频源地址为空");
        }

        CameraLiveStream liveStream = getOrCreateLiveStream(camera.getId());

        MediaServer mediaServer = getMediaServer(liveStream.getMediaServerId());

        if (LiveStreamConstants.STREAM_STATUS_ONLINE.equals(liveStream.getStreamStatus())) {
            return buildPlayVO(camera, mediaServer, liveStream);
        }

        if (LiveStreamConstants.STREAM_STATUS_STARTING.equals(liveStream.getStreamStatus())) {
            return buildPlayVO(camera, mediaServer, liveStream);
        }

        updateStreamStatus(liveStream.getId(), LiveStreamConstants.STREAM_STATUS_STARTING);
        liveStream.setStreamStatus(LiveStreamConstants.STREAM_STATUS_STARTING);

        try {
            zlmClient.addStreamProxy(
                    mediaServer,
                    liveStream.getStreamApp(),
                    liveStream.getStreamId(),
                    camera.getSourceUrl()
            );

            updateStreamStatus(liveStream.getId(), LiveStreamConstants.STREAM_STATUS_ONLINE);
            liveStream.setStreamStatus(LiveStreamConstants.STREAM_STATUS_ONLINE);
        } catch (Exception e) {
            updateStreamStatus(liveStream.getId(), LiveStreamConstants.STREAM_STATUS_CLOSED);
            liveStream.setStreamStatus(LiveStreamConstants.STREAM_STATUS_CLOSED);
            throw e;
        }

        return buildPlayVO(camera, mediaServer, liveStream);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleStreamChanged(ZlmStreamChangedDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getApp()) || !StringUtils.hasText(dto.getStream())) {
            return;
        }

        Integer streamStatus = Boolean.TRUE.equals(dto.getRegist())
                ? LiveStreamConstants.STREAM_STATUS_ONLINE
                : LiveStreamConstants.STREAM_STATUS_CLOSED;

        cameraLiveStreamMapper.update(null,
                new LambdaUpdateWrapper<CameraLiveStream>()
                        .eq(CameraLiveStream::getStreamApp, dto.getApp())
                        .eq(CameraLiveStream::getStreamId, dto.getStream())
                        .set(CameraLiveStream::getStreamStatus, streamStatus)
        );
    }

    private CameraLiveStream getOrCreateLiveStream(Long cameraId) {
        CameraLiveStream liveStream = cameraLiveStreamMapper.selectOne(
                new LambdaQueryWrapper<CameraLiveStream>()
                        .eq(CameraLiveStream::getCameraId, cameraId)
        );

        if (liveStream != null) {
            return liveStream;
        }

        MediaServer mediaServer = getDefaultMediaServer();

        CameraLiveStream insert = new CameraLiveStream();
        insert.setCameraId(cameraId);
        insert.setMediaServerId(mediaServer.getId());
        insert.setStreamApp(LiveStreamConstants.DEFAULT_STREAM_APP);
        insert.setStreamId(buildDefaultStreamId(cameraId));
        insert.setPlayProtocol(LiveStreamConstants.DEFAULT_PLAY_PROTOCOL);
        insert.setStreamStatus(LiveStreamConstants.STREAM_STATUS_CLOSED);
        insert.setCreateTime(LocalDateTime.now());

        try {
            cameraLiveStreamMapper.insert(insert);
            return insert;
        } catch (DuplicateKeyException e) {
            return cameraLiveStreamMapper.selectOne(
                    new LambdaQueryWrapper<CameraLiveStream>()
                            .eq(CameraLiveStream::getCameraId, cameraId)
            );
        }
    }

    private MediaServer getDefaultMediaServer() {
        MediaServer mediaServer = mediaServerMapper.selectOne(
                new LambdaQueryWrapper<MediaServer>()
                        .eq(MediaServer::getStatus, StatusEnum.ENABLED.getCode())
                        .orderByAsc(MediaServer::getId)
                        .last("LIMIT 1")
        );

        if (mediaServer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "未配置启用的流媒体服务器");
        }

        return mediaServer;
    }

    private MediaServer getMediaServer(Long mediaServerId) {
        MediaServer mediaServer = mediaServerMapper.selectById(mediaServerId);
        if (mediaServer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "流媒体服务器不存在");
        }

        if (!StatusEnum.isEnabled(mediaServer.getStatus())) {
            throw new BusinessException("流媒体服务器已禁用");
        }

        return mediaServer;
    }

    private void updateStreamStatus(Long id, Integer streamStatus) {
        cameraLiveStreamMapper.update(null,
                new LambdaUpdateWrapper<CameraLiveStream>()
                        .eq(CameraLiveStream::getId, id)
                        .set(CameraLiveStream::getStreamStatus, streamStatus)
        );
    }

    private String buildDefaultStreamId(Long cameraId) {
        return "camera_" + cameraId;
    }

    private CameraLivePlayVO buildPlayVO(CameraVO camera,
                                         MediaServer mediaServer,
                                         CameraLiveStream liveStream) {
        CameraLivePlayVO vo = new CameraLivePlayVO();
        vo.setCameraId(camera.getId());
        vo.setCameraName(camera.getCameraName());
        vo.setCameraCode(camera.getCameraCode());
        vo.setMediaServerId(mediaServer.getId());
        vo.setStreamApp(liveStream.getStreamApp());
        vo.setStreamId(liveStream.getStreamId());
        vo.setPlayProtocol(liveStream.getPlayProtocol());
        vo.setStreamStatus(liveStream.getStreamStatus());
        vo.setPlayUrl(zlmClient.buildPlayUrl(
                mediaServer,
                liveStream.getStreamApp(),
                liveStream.getStreamId(),
                liveStream.getPlayProtocol()
        ));
        return vo;
    }
}