package com.sk.iba.module.device.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk.iba.common.constant.AlgorithmRuntimeConstants;
import com.sk.iba.common.exception.BusinessException;
import com.sk.iba.module.device.entity.AlgorithmServer;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 算法运行接口客户端
 *
 * @author zzy
 */
@Component
@RequiredArgsConstructor
public class AlgorithmRuntimeClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 启动算法
     */
    public void start(AlgorithmServer server,
                      String deployPath,
                      String functionCode,
                      String runtimeEnv,
                      String startFileName,
                      String weightPath) {
        post(server, "start", buildBody(deployPath, functionCode, runtimeEnv, startFileName, weightPath));
    }

    /**
     * 停止算法
     */
    public void stop(AlgorithmServer server,
                     String deployPath,
                     String functionCode,
                     String runtimeEnv,
                     String startFileName,
                     String weightPath) {
        post(server, "stop", buildBody(deployPath, functionCode, runtimeEnv, startFileName, weightPath));
    }

    private Map<String, Object> buildBody(String deployPath,
                                          String functionCode,
                                          String runtimeEnv,
                                          String startFileName,
                                          String weightPath) {
        Map<String, Object> body = new HashMap<>();
        body.put("algorithmPath", deployPath);
        body.put("algorithmType", functionCode);
        body.put("algorithmEnv", runtimeEnv);
        body.put("algorithmFile", startFileName);
        body.put("weights", weightPath);
        return body;
    }

    private void post(AlgorithmServer server, String api, Map<String, Object> body) {
        if (!StringUtils.hasText(server.getIp())) {
            throw new BusinessException("算法服务器IP不能为空");
        }

        try {
            String requestJson = objectMapper.writeValueAsString(body);

            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host(server.getIp())
                    .port(AlgorithmRuntimeConstants.ALGORITHM_API_PORT)
                    .addPathSegment(api)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(requestJson, JSON))
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new BusinessException("调用算法服务失败，状态码：" + response.code());
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    return;
                }

                String result = responseBody.string();
                if (!StringUtils.hasText(result)) {
                    return;
                }

                JsonNode responseNode = objectMapper.readTree(result);
                JsonNode codeNode = responseNode.get("code");
                if (codeNode != null
                        && !"0".equals(codeNode.asText())
                        && !"200".equals(codeNode.asText())) {
                    String msg = responseNode.path("msg").asText("未知错误");
                    throw new BusinessException("调用算法服务失败：" + msg);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("调用算法服务失败：" + e.getMessage());
        }
    }
}