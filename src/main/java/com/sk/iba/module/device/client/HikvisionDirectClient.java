package com.sk.iba.module.device.client;

import com.sk.iba.common.exception.BusinessException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 海康直连摄像头 ISAPI 客户端
 *
 * @author zzy
 */
@Component
public class HikvisionDirectClient {

    /**
     * ISAPI 默认 HTTP 端口
     */
    private static final int ISAPI_HTTP_PORT = 80;

    /**
     * 获取设备信息
     */
    public String getDeviceInfo(String ip, String username, String password) {
        return doGet(ip, username, password, "/ISAPI/System/deviceInfo");
    }

    /**
     * 获取码流通道列表
     */
    public String getStreamingChannels(String ip, String username, String password) {
        return doGet(ip, username, password, "/ISAPI/Streaming/channels");
    }

    private String doGet(String ip, String username, String password, String path) {
        try (CloseableHttpClient httpClient = buildHttpClient(ip, username, password)) {
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(ip)
                    .setPort(ISAPI_HTTP_PORT)
                    .setPath(path)
                    .build();

            HttpGet request = new HttpGet(uri);
            request.setHeader(HttpHeaders.ACCEPT, "application/xml");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    throw new BusinessException("摄像头认证失败，请检查账号或密码");
                }
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    throw new BusinessException("摄像头不支持该 ISAPI 接口");
                }
                if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                    throw new BusinessException("摄像头请求失败，状态码：" + statusCode);
                }

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new BusinessException("摄像头请求失败：无响应");
                }

                String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                if (!StringUtils.hasText(body)) {
                    throw new BusinessException("摄像头请求失败：响应为空");
                }

                return body;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("连接摄像头失败：" + e.getMessage());
        }
    }

    private CloseableHttpClient buildHttpClient(String ip, String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(ip, ISAPI_HTTP_PORT),
                new UsernamePasswordCredentials(username, password)
        );

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(3000)
                .setSocketTimeout(8000)
                .build();

        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}