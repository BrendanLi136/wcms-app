package com.wcms.common.config;

import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;

    /**
     * 初始化七牛云配置
     *
     * @return 七牛云配置对象
     */
    @Bean
    public Configuration qiniuConfiguration() {
        // 自动识别存储区域
        return new Configuration(Zone.autoZone());
    }

    /**
     * 初始化七牛云上传管理器
     *
     * @param configuration 七牛云配置
     * @return 七牛云上传管理器
     */
    @Bean
    public UploadManager uploadManager(Configuration configuration) {
        return new UploadManager(configuration);
    }

    /**
     * 初始化七牛云认证
     *
     * @return 七牛云认证对象
     */
    @Bean
    public Auth auth() {
        return Auth.create(accessKey, secretKey);
    }
}