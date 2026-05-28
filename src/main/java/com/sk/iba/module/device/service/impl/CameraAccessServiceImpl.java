package com.sk.iba.module.device.service.impl;

import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.client.HikvisionDirectClient;
import com.sk.iba.module.device.dto.CameraDirectProbeDTO;
import com.sk.iba.module.device.service.CameraAccessService;
import com.sk.iba.module.device.vo.CameraDirectProbeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 摄像头接入 Service 实现
 *
 * @author zzy
 */
@Service
@RequiredArgsConstructor
public class CameraAccessServiceImpl implements CameraAccessService {

    /**
     * 视频源类型：RTSP
     */
    private static final Integer SOURCE_TYPE_RTSP = 1;

    /**
     * 默认 RTSP 端口
     */
    private static final Integer DEFAULT_RTSP_PORT = 554;

    private final HikvisionDirectClient hikvisionDirectClient;

    @Override
    public CameraDirectProbeVO directProbe(CameraDirectProbeDTO probeDTO) {
        String ip = probeDTO.getIp().trim();
        String username = probeDTO.getUsername().trim();
        String password = probeDTO.getPassword();
        Integer rtspPort = probeDTO.getPort() == null ? DEFAULT_RTSP_PORT : probeDTO.getPort();

        String deviceInfoXml = hikvisionDirectClient.getDeviceInfo(ip, username, password);
        String channelsXml = hikvisionDirectClient.getStreamingChannels(ip, username, password);

        Document deviceDoc = parseXml(deviceInfoXml);
        Document channelsDoc = parseXml(channelsXml);

        String serialNumber = getText(deviceDoc, "serialNumber");
        String deviceName = getText(deviceDoc, "deviceName");

        Element streamElement = getDefaultStreamElement(channelsDoc);
        String streamId = getDirectChildText(streamElement, "id");
        String channelName = getDirectChildText(streamElement, "channelName");

        String cameraName = StringUtils.hasText(channelName) ? channelName : deviceName;
        if (!StringUtils.hasText(cameraName)) {
            cameraName = ip;
        }

        CameraDirectProbeVO vo = new CameraDirectProbeVO();
        vo.setCameraName(cameraName);
        vo.setCameraCode(buildCameraCode(serialNumber));
        vo.setSourceType(SOURCE_TYPE_RTSP);
        vo.setSourceUrl(buildRtspUrl(ip, rtspPort, username, password, streamId));
        vo.setIp(ip);
        vo.setPort(rtspPort);
        vo.setUsername(username);
        vo.setPassword(password);
        return vo;
    }

    private Element getDefaultStreamElement(Document doc) {
        NodeList channelNodes = doc.getElementsByTagName("StreamingChannel");
        Element firstAvailable = null;

        for (int i = 0; i < channelNodes.getLength(); i++) {
            Node node = channelNodes.item(i);
            if (!(node instanceof Element element)) {
                continue;
            }

            String enabled = getDirectChildText(element, "enabled");
            if (!"true".equalsIgnoreCase(enabled)) {
                continue;
            }

            if (!supportRtsp(element)) {
                continue;
            }

            String streamId = getDirectChildText(element, "id");
            if (!StringUtils.hasText(streamId)) {
                continue;
            }

            if (firstAvailable == null) {
                firstAvailable = element;
            }

            if ("101".equals(streamId)) {
                return element;
            }
        }

        if (firstAvailable == null) {
            throw new BusinessException("未获取到可用码流");
        }

        return firstAvailable;
    }

    private boolean supportRtsp(Element channelElement) {
        NodeList transportNodes = channelElement.getElementsByTagName("streamingTransport");
        if (transportNodes.getLength() == 0) {
            return true;
        }

        for (int i = 0; i < transportNodes.getLength(); i++) {
            String value = transportNodes.item(i).getTextContent();
            if ("RTSP".equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    private String buildRtspUrl(String ip,
                                Integer rtspPort,
                                String username,
                                String password,
                                String streamId) {
        return "rtsp://"
                + encodeUrlPart(username)
                + ":"
                + encodeUrlPart(password)
                + "@"
                + ip
                + ":"
                + rtspPort
                + "/Streaming/Channels/"
                + streamId;
    }

    private String encodeUrlPart(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    private String buildCameraCode(String serialNumber) {
        if (StringUtils.hasText(serialNumber)) {
            return serialNumber.trim();
        }

        return "序列号为空，请手动输入";
    }

    private Document parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            disableExternalEntity(factory);

            return factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new BusinessException("解析摄像头返回数据失败");
        }
    }

    private void disableExternalEntity(DocumentBuilderFactory factory) {
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
        } catch (Exception ignored) {
        }
    }

    private String getText(Document doc, String tagName) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            return null;
        }

        return trimToNull(nodeList.item(0).getTextContent());
    }

    private String getDirectChildText(Element element, String tagName) {
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element childElement && tagName.equals(childElement.getTagName())) {
                return trimToNull(childElement.getTextContent());
            }
        }

        return null;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}