package com.sk.iba.module.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sk.iba.common.enums.ResultCode;
import com.sk.iba.common.enums.StatusEnum;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.common.page.PageResult;
import com.sk.iba.module.monitor.dto.MediaServerCreateDTO;
import com.sk.iba.module.monitor.dto.MediaServerQueryDTO;
import com.sk.iba.module.monitor.dto.MediaServerUpdateDTO;
import com.sk.iba.module.monitor.entity.MediaServer;
import com.sk.iba.module.monitor.mapper.MediaServerMapper;
import com.sk.iba.module.monitor.service.MediaServerService;
import com.sk.iba.module.monitor.vo.MediaServerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 流媒体服务器 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class MediaServerServiceImpl implements MediaServerService {

    private final MediaServerMapper mediaServerMapper;

    @Override
    public PageResult<MediaServerVO> pageMediaServers(MediaServerQueryDTO queryDTO) {
        Page<MediaServer> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<MediaServer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getServerName()), MediaServer::getServerName, queryDTO.getServerName())
                .eq(queryDTO.getStatus() != null, MediaServer::getStatus, queryDTO.getStatus())
                .orderByDesc(MediaServer::getCreateTime);

        Page<MediaServer> mediaServerPage = mediaServerMapper.selectPage(page, wrapper);
        IPage<MediaServerVO> voPage = mediaServerPage.convert(this::toVO);
        return PageResult.of(voPage);
    }

    @Override
    public MediaServerVO getMediaServerById(Long id) {
        MediaServer mediaServer = getMediaServer(id);
        return toVO(mediaServer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMediaServer(MediaServerCreateDTO createDTO) {
        MediaServer mediaServer = new MediaServer();
        BeanUtils.copyProperties(createDTO, mediaServer);

        trimCreateFields(mediaServer);

        if (mediaServer.getStatus() == null) {
            mediaServer.setStatus(StatusEnum.ENABLED.getCode());
        }

        if (!StatusEnum.isValid(mediaServer.getStatus())) {
            throw new BusinessException("流媒体服务器状态不合法");
        }

        mediaServerMapper.insert(mediaServer);
        return mediaServer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMediaServer(MediaServerUpdateDTO updateDTO) {
        getMediaServer(updateDTO.getId());

        if (updateDTO.getStatus() != null && !StatusEnum.isValid(updateDTO.getStatus())) {
            throw new BusinessException("流媒体服务器状态不合法");
        }

        MediaServer mediaServer = new MediaServer();
        BeanUtils.copyProperties(updateDTO, mediaServer);

        trimUpdateFields(mediaServer);

        mediaServerMapper.updateById(mediaServer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMediaServer(Long id) {
        MediaServer mediaServer = getMediaServer(id);
        mediaServerMapper.deleteById(mediaServer.getId());
    }

    private MediaServer getMediaServer(Long id) {
        MediaServer mediaServer = mediaServerMapper.selectById(id);
        if (mediaServer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "流媒体服务器不存在");
        }
        return mediaServer;
    }

    private void trimCreateFields(MediaServer mediaServer) {
        mediaServer.setServerName(mediaServer.getServerName().trim());
        mediaServer.setApiBaseUrl(trimEndSlash(mediaServer.getApiBaseUrl().trim()));
        mediaServer.setSecret(mediaServer.getSecret().trim());
        mediaServer.setHttpPlayBaseUrl(trimEndSlash(mediaServer.getHttpPlayBaseUrl().trim()));

        if (StringUtils.hasText(mediaServer.getWsPlayBaseUrl())) {
            mediaServer.setWsPlayBaseUrl(trimEndSlash(mediaServer.getWsPlayBaseUrl().trim()));
        }

        if (StringUtils.hasText(mediaServer.getRtcPlayBaseUrl())) {
            mediaServer.setRtcPlayBaseUrl(trimEndSlash(mediaServer.getRtcPlayBaseUrl().trim()));
        }

        if (StringUtils.hasText(mediaServer.getRemark())) {
            mediaServer.setRemark(mediaServer.getRemark().trim());
        }
    }

    private void trimUpdateFields(MediaServer mediaServer) {
        if (mediaServer.getServerName() != null) {
            if (!StringUtils.hasText(mediaServer.getServerName())) {
                throw new BusinessException("流媒体服务器名称不能为空");
            }
            mediaServer.setServerName(mediaServer.getServerName().trim());
        }

        if (mediaServer.getApiBaseUrl() != null) {
            if (!StringUtils.hasText(mediaServer.getApiBaseUrl())) {
                throw new BusinessException("ZLM API地址不能为空");
            }
            mediaServer.setApiBaseUrl(trimEndSlash(mediaServer.getApiBaseUrl().trim()));
        }

        if (mediaServer.getSecret() != null) {
            if (!StringUtils.hasText(mediaServer.getSecret())) {
                throw new BusinessException("ZLM API密钥不能为空");
            }
            mediaServer.setSecret(mediaServer.getSecret().trim());
        }

        if (mediaServer.getHttpPlayBaseUrl() != null) {
            if (!StringUtils.hasText(mediaServer.getHttpPlayBaseUrl())) {
                throw new BusinessException("HTTP播放基础地址不能为空");
            }
            mediaServer.setHttpPlayBaseUrl(trimEndSlash(mediaServer.getHttpPlayBaseUrl().trim()));
        }

        if (mediaServer.getWsPlayBaseUrl() != null && StringUtils.hasText(mediaServer.getWsPlayBaseUrl())) {
            mediaServer.setWsPlayBaseUrl(trimEndSlash(mediaServer.getWsPlayBaseUrl().trim()));
        }

        if (mediaServer.getRtcPlayBaseUrl() != null && StringUtils.hasText(mediaServer.getRtcPlayBaseUrl())) {
            mediaServer.setRtcPlayBaseUrl(trimEndSlash(mediaServer.getRtcPlayBaseUrl().trim()));
        }

        if (mediaServer.getRemark() != null && StringUtils.hasText(mediaServer.getRemark())) {
            mediaServer.setRemark(mediaServer.getRemark().trim());
        }
    }

    private String trimEndSlash(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }

        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    private MediaServerVO toVO(MediaServer mediaServer) {
        MediaServerVO vo = new MediaServerVO();
        BeanUtils.copyProperties(mediaServer, vo);
        return vo;
    }
}