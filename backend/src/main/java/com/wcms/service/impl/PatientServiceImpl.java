package com.wcms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.common.utils.WeChatUtil;
import com.wcms.domain.entity.Patient;
import com.wcms.mapper.PatientMapper;
import com.wcms.service.PatientService;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    private final WeChatUtil weChatUtil;

    @Override
    public Patient getByOpenId(String openid) {
        return this.getOne(new LambdaQueryWrapper<Patient>().eq(Patient::getOpenid, openid));
    }

    @Override
    public Patient loginByCode(String code) {
        // Use WeChatUtil to get real OpenID
        cn.hutool.json.JSONObject session = weChatUtil.getSessionInfo(code);
        String openid = session.getStr("openid");

        // Mock fallback if keys not set (for safety during dev if params empty)
        if (openid == null) openid = "mock_openid_" + code;

        Patient patient = this.getByOpenId(openid);
        if (patient == null) {
            patient = new Patient();
            patient.setOpenid(openid);
            patient.setCreateTime(java.time.LocalDateTime.now());
            this.save(patient);
        }
        return patient;
    }

    @Override
    public Patient updatePhone(Long userId, String code, String encryptedData, String iv) {
        // 1. Get SessionKey (needs code)
        // Note: In production, session_key should be stored in Redis/DB mapped to a
        // custom session token
        // passed to frontend. Here, we simplistically ask frontend to send code again
        // to refresh session_key.
        cn.hutool.json.JSONObject session = weChatUtil.getSessionInfo(code);
        String sessionKey = session.getStr("session_key");

        // 2. Decrypt
        String phone = weChatUtil.decryptPhone(sessionKey, encryptedData, iv);

        Patient patient = this.getById(userId);
        if (patient != null) {
            patient.setPhone(phone);
            this.updateById(patient);
            return patient;
        }
        return null;
    }

    @Override
    public IPage<Patient> getPatientPage(Page<Patient> page, String name) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Patient::getName, name);
        }
        wrapper.orderByDesc(Patient::getCreateTime);
        return this.page(page, wrapper);
    }
}
