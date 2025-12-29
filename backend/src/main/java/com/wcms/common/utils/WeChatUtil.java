package com.wcms.common.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;

@Slf4j
@Component
public class WeChatUtil {

    @Value("${wcms.wechat.appid}")
    private String appid;

    @Value("${wcms.wechat.secret}")
    private String secret;

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";

    /**
     * Get OpenID and SessionKey by Code
     */
    public JSONObject getSessionInfo(String code) {
        String url = cn.hutool.core.util.StrUtil.format(CODE2SESSION_URL, appid, secret, code);
        String response = HttpUtil.get(url);
        log.info("WeChat Login Response: {}", response);
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            throw new RuntimeException("WeChat Login Failed: " + json.getStr("errmsg"));
        }
        return json;
    }

    /**
     * Decrypt Phone Number
     */
    public String decryptPhone(String sessionKey, String encryptedData, String iv) {
        try {
            byte[] keyBytes = Base64.decode(sessionKey);
            byte[] dataBytes = Base64.decode(encryptedData);
            byte[] ivBytes = Base64.decode(iv);

            SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivBytes));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] result = cipher.doFinal(dataBytes);

            String jsonStr = new String(result, StandardCharsets.UTF_8);
            JSONObject json = JSONUtil.parseObj(jsonStr);
            return json.getStr("phoneNumber");
        } catch (Exception e) {
            log.error("Decrypt Phone Failed", e);
            throw new RuntimeException("Decrypt Phone Failed");
        }
    }
}
