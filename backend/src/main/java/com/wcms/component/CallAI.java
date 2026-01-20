package com.wcms.component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wcms.domain.dto.AnalysisDTO;
import com.wcms.domain.entity.AiConfig;
import com.wcms.service.AiConfigService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CallAI {
    @Autowired
    private AiConfigService aiConfigService;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();
    private static final String WORKFLOW_ID = "7597420392920612906";

    public AnalysisDTO call(String imgUrl, String prompt) throws Exception {

        AiConfig aiConfig = aiConfigService.getActiveConfig();
        if (aiConfig == null) {
            log.error("No active AI config found！！！！！！！！！！！！！！！！！！！！！！！");
            return null;
        }
        String url = aiConfig.getModelUrl();
        String apiKey = aiConfig.getApiKey();
        // 构建请求体
        String requestBody = String.format(
                "{\n" +
                        "  \"workflow_id\": \"%s\"," +
                        "  \"parameters\": {\n" +
                        "    \"imgUrl\":  \"%s\"" +
                        "  }\n" +
                        "}",
                WORKFLOW_ID, imgUrl
        );

        // 创建HTTP请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(600))
                .build();
        // 发送请求并获取响应
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        log.info("AI Response: {}", body);
        return parseResult(body);
    }

    public static void main(String[] args) {
        String msg = "{\"data\":\"{\\\"analysis_result\\\":\\\"伤口位于皮肤表面，表皮破损，有渗血或组织液，创面上可见粗糙的出血区域，符合擦伤特征，通常为表面损伤。\\\",\\\"wound_type\\\":\\\"擦伤\\\"}\",\"debug_url\":\"https://www.coze.cn/work_flow?execute_id=7597439862330114057&space_id=7552357646402584616&workflow_id=7597420392920612906&execute_mode=2\",\"usage\":{\"token_count\":1570,\"output_count\":53,\"input_count\":1517},\"execute_id\":\"7597439862330114057\",\"detail\":{\"logid\":\"20260120214656188E3675A40D0D9AF37E\"},\"code\":0,\"msg\":\"\"}";

        AnalysisDTO analysisDTO = parseResult(msg);
    }

    public static AnalysisDTO parseResult(String result) {
        try {
            // 解析外层JSON
            JSONObject rootObj = JSON.parseObject(result);

            // 获取messages数组
            JSONObject data = rootObj.getJSONObject("data");

            // 提取所需字段
            String woundType = data.getString("wound_type");
            String analysisResult = data.getString("analysis_result");

            System.out.println("伤口类型: " + woundType);
            System.out.println("分析结果: " + analysisResult);
            return new AnalysisDTO(woundType, analysisResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("解析结果失败", e);
        }
    }
}
