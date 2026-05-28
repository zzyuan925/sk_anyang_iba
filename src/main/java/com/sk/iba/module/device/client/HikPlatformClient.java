package com.sk.iba.module.device.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.hikvision.artemis.sdk.constant.Constants;
import com.sk.iba.common.constant.HikPlatformConstants;
import com.sk.iba.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 海康综合平台客户端
 *
 * @author zzy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HikPlatformClient {

    /**
     * 分页获取监控点资源
     */
    private static final String CAMERA_SEARCH_API = "/api/resource/v2/camera/search";

    /**
     * 获取监控点预览取流URL
     */
    private static final String CAMERA_PREVIEW_URL_API = "/api/video/v2/cameras/previewURLs";

    /**
     * 默认页码
     */
    private static final Integer DEFAULT_PAGE_NO = 1;

    /**
     * 默认分页大小
     */
    private static final Integer DEFAULT_PAGE_SIZE = 1000;

    /**
     * 默认码流类型：0 主码流
     */
    private static final Integer DEFAULT_STREAM_TYPE = 0;

    /**
     * 默认取流协议
     */
    private static final String DEFAULT_PROTOCOL = "rtsp";

    /**
     * 默认传输模式：1 TCP
     */
    private static final Integer DEFAULT_TRANSMODE = 1;

    private final ObjectMapper objectMapper;

    static {
        Constants.DEFAULT_TIMEOUT = HikPlatformConstants.CONNECT_TIMEOUT;
        Constants.SOCKET_TIMEOUT = HikPlatformConstants.SOCKET_TIMEOUT;
    }

    /**
     * 根据摄像头名称从平台分页获取监控点资源
     *
     * @param cameraName 摄像头名称，模糊搜索，可为空
     * @return 平台响应
     */
    public JsonNode searchCameraByName(String cameraName) {
        return searchCamera(cameraName, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
    }

    /**
     * 分页获取监控点资源
     *
     * @param cameraName 摄像头名称，模糊搜索，可为空
     * @param pageNo 当前页码
     * @param pageSize 分页大小
     * @return 平台响应
     */
    public JsonNode searchCamera(String cameraName, Integer pageNo, Integer pageSize) {
        Map<String, Object> body = new HashMap<>();
        body.put("pageNo", pageNo == null ? DEFAULT_PAGE_NO : pageNo);
        body.put("pageSize", pageSize == null ? DEFAULT_PAGE_SIZE : pageSize);

        if (StringUtils.hasText(cameraName)) {
            body.put("name", cameraName.trim());
        }

        return postJsonForNode(CAMERA_SEARCH_API, body);
    }

    /**
     * 获取监控点预览取流URL，默认 RTSP + 主码流
     *
     * @param cameraIndexCode 监控点唯一标识
     * @return 平台响应
     */
    public JsonNode getPreviewUrl(String cameraIndexCode) {
        return getPreviewUrl(cameraIndexCode, DEFAULT_STREAM_TYPE, DEFAULT_PROTOCOL);
    }

    /**
     * 获取监控点预览取流URL
     *
     * @param cameraIndexCode 监控点唯一标识
     * @param streamType 码流类型
     * @param protocol 取流协议，例如 rtsp
     * @return 平台响应
     */
    public JsonNode getPreviewUrl(String cameraIndexCode, Integer streamType, String protocol) {
        if (!StringUtils.hasText(cameraIndexCode)) {
            throw new BusinessException("监控点唯一标识不能为空");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("cameraIndexCode", cameraIndexCode.trim());
        body.put("streamType", streamType == null ? DEFAULT_STREAM_TYPE : streamType);
        body.put("protocol", StringUtils.hasText(protocol) ? protocol : DEFAULT_PROTOCOL);
        body.put("transmode", DEFAULT_TRANSMODE);

        return postJsonForNode(CAMERA_PREVIEW_URL_API, body);
    }

    /**
     * POST JSON 请求，返回原始字符串
     */
    public String postJson(String apiPath, Object body) {
        try {
            String requestBody = body == null ? "{}" : objectMapper.writeValueAsString(body);

            log.info("调用海康平台接口，apiPath={}, body={}", apiPath, requestBody);

            return ArtemisHttpUtil.doPostStringArtemis(
                    buildConfig(),
                    buildPath(apiPath),
                    requestBody,
                    null,
                    null,
                    HikPlatformConstants.CONTENT_TYPE_JSON
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("海康平台请求失败：" + e.getMessage());
        }
    }

    /**
     * POST JSON 请求，返回 JsonNode
     */
    public JsonNode postJsonForNode(String apiPath, Object body) {
        String response = postJson(apiPath, body);

        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new BusinessException("解析海康平台响应失败：" + e.getMessage());
        }
    }

    private ArtemisConfig buildConfig() {
        if (!StringUtils.hasText(HikPlatformConstants.HOST)) {
            throw new BusinessException("海康平台 HOST 未配置");
        }
        if (!StringUtils.hasText(HikPlatformConstants.APP_KEY)) {
            throw new BusinessException("海康平台 APP_KEY 未配置");
        }
        if (!StringUtils.hasText(HikPlatformConstants.APP_SECRET)) {
            throw new BusinessException("海康平台 APP_SECRET 未配置");
        }

        ArtemisConfig config = new ArtemisConfig();
        config.setHost(HikPlatformConstants.HOST);
        config.setAppKey(HikPlatformConstants.APP_KEY);
        config.setAppSecret(HikPlatformConstants.APP_SECRET);
        return config;
    }

    private Map<String, String> buildPath(String apiPath) {
        if (!StringUtils.hasText(apiPath)) {
            throw new BusinessException("海康平台接口地址不能为空");
        }

        String path = apiPath.startsWith(HikPlatformConstants.ARTEMIS_PATH)
                ? apiPath
                : HikPlatformConstants.ARTEMIS_PATH + apiPath;

        Map<String, String> pathMap = new HashMap<>(1);
        pathMap.put(getProtocolPrefix(), path);
        return pathMap;
    }

    private String getProtocolPrefix() {
        if ("http".equalsIgnoreCase(HikPlatformConstants.PROTOCOL)) {
            return "http://";
        }

        return "https://";
    }
}