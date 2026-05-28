package com.sk.iba.module.monitor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.iba.common.constant.LiveStreamConstants;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.monitor.entity.MediaServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * ZLMediaKit API 客户端
 *
 * @author zzy
 */
@Component
public class ZlmClient {

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 添加 RTSP 拉流代理
     */
    public void addStreamProxy(MediaServer mediaServer,
                               String streamApp,
                               String streamId,
                               String sourceUrl) {
        HttpUrl baseUrl = HttpUrl.parse(trimEndSlash(mediaServer.getApiBaseUrl()) + "/index/api/addStreamProxy");
        if (baseUrl == null) {
            throw new BusinessException("ZLM API 地址不合法");
        }

        HttpUrl url = baseUrl.newBuilder()
                .addQueryParameter("secret", mediaServer.getSecret())
                .addQueryParameter("vhost", LiveStreamConstants.DEFAULT_VHOST)
                .addQueryParameter("app", streamApp)
                .addQueryParameter("stream", streamId)
                .addQueryParameter("url", sourceUrl)
                .addQueryParameter("enable_hls", "0")
                .addQueryParameter("enable_mp4", "0")
                .addQueryParameter("rtp_type", "0")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new BusinessException("ZLM 拉流失败，状态码：" + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new BusinessException("ZLM 拉流失败：无响应");
            }

            JsonNode responseNode = objectMapper.readTree(responseBody.string());
            int code = responseNode.path("code").asInt(-1);
            if (code != 0) {
                String msg = responseNode.path("msg").asText("未知错误");
                if (isStreamAlreadyExists(msg)) {
                    return;
                }
                throw new BusinessException("ZLM 拉流失败：" + msg);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("ZLM 拉流失败：" + e.getMessage());
        }
    }

    private boolean isStreamAlreadyExists(String msg) {
        return StringUtils.hasText(msg)
                && msg.toLowerCase().contains("stream already exists");
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