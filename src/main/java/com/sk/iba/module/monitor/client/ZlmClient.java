package com.sk.iba.module.monitor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.sk.iba.common.constant.LiveStreamConstants;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.monitor.entity.MediaServer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * ZLMediaKit API 客户端
 *
 * @author zzy
 */
@Component
public class ZlmClient {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 添加 RTSP 拉流代理
     */
    public void addStreamProxy(MediaServer mediaServer,
                               String streamApp,
                               String streamId,
                               String sourceUrl) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(trimEndSlash(mediaServer.getApiBaseUrl()))
                .path("/index/api/addStreamProxy")
                .queryParam("secret", mediaServer.getSecret())
                .queryParam("vhost", LiveStreamConstants.DEFAULT_VHOST)
                .queryParam("app", streamApp)
                .queryParam("stream", streamId)
                .queryParam("url", sourceUrl)
                .queryParam("enable_hls", 0)
                .queryParam("enable_mp4", 0)
                .queryParam("rtp_type", 0)
                .build()
                .encode()
                .toUri();

        JsonNode response = restTemplate.getForObject(uri, JsonNode.class);
        if (response == null) {
            throw new BusinessException("ZLM 拉流失败：无响应");
        }

        int code = response.path("code").asInt(-1);
        if (code != 0) {
            String msg = response.path("msg").asText("未知错误");
            throw new BusinessException("ZLM 拉流失败：" + msg);
        }
    }

    public String buildPlayUrl(MediaServer mediaServer,
                               String streamApp,
                               String streamId,
                               String playProtocol) {
        if ("ws-flv".equalsIgnoreCase(playProtocol)) {
            if (!StringUtils.hasText(mediaServer.getWsPlayBaseUrl())) {
                throw new BusinessException("流媒体服务器未配置 WebSocket 播放地址");
            }
            return trimEndSlash(mediaServer.getWsPlayBaseUrl()) + "/" + streamApp + "/" + streamId + ".live.flv";
        }

        if ("hls".equalsIgnoreCase(playProtocol)) {
            return trimEndSlash(mediaServer.getHttpPlayBaseUrl()) + "/" + streamApp + "/" + streamId + "/hls.m3u8";
        }

        if ("webrtc".equalsIgnoreCase(playProtocol)) {
            if (!StringUtils.hasText(mediaServer.getRtcPlayBaseUrl())) {
                throw new BusinessException("流媒体服务器未配置 WebRTC 播放地址");
            }
            return trimEndSlash(mediaServer.getRtcPlayBaseUrl())
                    + "/index/api/webrtc?app=" + streamApp
                    + "&stream=" + streamId
                    + "&type=play";
        }

        return trimEndSlash(mediaServer.getHttpPlayBaseUrl()) + "/" + streamApp + "/" + streamId + ".live.flv";
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
}