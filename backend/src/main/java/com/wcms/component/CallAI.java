package com.wcms.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.wcms.common.utils.TipUtil;
import com.wcms.domain.dto.AIReq;
import com.wcms.domain.dto.AnalysisDTO;
import com.wcms.domain.entity.AiConfig;
import com.wcms.service.AiConfigService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CallAI {
    @Autowired
    private AiConfigService aiConfigService;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    public AnalysisDTO call(String imgUrl, String prompt) throws Exception {

        AiConfig aiConfig = aiConfigService.getActiveConfig();
        if (aiConfig == null) {
            log.error("No active AI config found！！！！！！！！！！！！！！！！！！！！！！！");
            return null;
        }
        String model = aiConfig.getModelType();
        String url = aiConfig.getModelUrl();
        String apiKey = aiConfig.getApiKey();
        AIReq aiReq = AIReq.build(model, prompt, imgUrl);
        String jsonStr = JSONUtil.toJsonStr(aiReq);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        Request request = new Request.Builder().url(url).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey).build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String responseBody = response.body().string();
        log.info("AI Response: {}", responseBody);
        return parseResult(responseBody);

    }

    private AnalysisDTO parseResult(String jsonStr) throws Exception {
        try {
            // 1. 解析原始JSON为JSONObject（Hutool核心API：JSONUtil.parseObj()）
            JSONObject rootObj = JSONUtil.parseObj(jsonStr);

            // 2. 逐层提取content字符串：root -> choices（JSON数组）-> 索引0 -> message -> content
            // 获取choices数组
            JSONArray choicesArray = rootObj.getJSONArray("choices");
            // 获取数组第一个元素（索引0）
            JSONObject firstChoiceObj = choicesArray.getJSONObject(0);
            // 获取message对象
            JSONObject messageObj = firstChoiceObj.getJSONObject("message");
            // 提取content字段的字符串内容
            String contentJsonStr = messageObj.getStr("content");

            // 3. 解析content字符串为JSONObject，提取目标字段
            JSONObject contentObj = JSONUtil.parseObj(contentJsonStr);
            // 提取wound_type字段
            String woundType = contentObj.getStr("wound_type");
            // 提取analysis_result字段
            String analysisResult = contentObj.getStr("analysis_result");

            // 4. 输出结果
            System.out.println("wound_type: " + woundType);
            System.out.println("analysis_result: " + analysisResult);
            return new AnalysisDTO(woundType, analysisResult);
        } catch (Exception e) {
            throw new Exception("解析AI返回结果失败！" + e.getMessage());
        }
    }
}
