package com.wcms.component;

import cn.hutool.json.JSONUtil;
import com.wcms.common.utils.TipUtil;
import com.wcms.domain.dto.AIReq;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CallForQianfan {
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().readTimeout(300, TimeUnit.SECONDS).build();

    public static void main(String[] args) throws IOException {
        String tip = TipUtil.getTip("20", "男", "无");
        System.out.println(tip);
        AIReq aiReq = AIReq.build("qianfan-multipicocr", tip, "http://t7vecgtjo.hn-bkt.clouddn.com/2544b2ff-ee5f-47bd-be11-cfbd93cbb686.png");
        String jsonStr = JSONUtil.toJsonStr(aiReq);
        System.out.println(jsonStr);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonStr);
        Request request = new Request.Builder()
                .url("https://qianfan.baidubce.com/v2/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer bce-v3/ALTAK-ZZVwlCegdvWESiOzeyidH/a3de2c2445bafbfa0e0efd97d0f75b1b2285d1fe")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());

    }
}
