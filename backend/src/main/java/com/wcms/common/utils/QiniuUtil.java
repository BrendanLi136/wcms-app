package com.wcms.common.utils;

import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import com.wcms.common.config.QiniuConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 七牛云上传工具类
 */
@Component
public class QiniuUtil {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private Auth auth;

    @Autowired
    private QiniuConfig qiniuConfig;

    /**
     * 生成上传凭证
     *
     * @return 上传凭证
     */
    private String getUploadToken() {
        return auth.uploadToken(qiniuConfig.getBucket());
    }

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 访问文件的URL
     * @throws Exception 上传异常
     */
    public String uploadFile(MultipartFile file, String patientId) throws Exception {
        // 生成唯一文件名
        String key = getPath(patientId, file.getOriginalFilename());

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            byte[] data = IOUtils.toByteArray(inputStream);
            Response response = uploadManager.put(data, key, getUploadToken());

            // 检查上传结果
            if (response.isOK()) {
                // 返回完整的文件URL
                return String.format("http://%s/%s", qiniuConfig.getDomain(), key);
            } else {
                throw new RuntimeException("七牛云上传失败：" + response.bodyString());
            }
        }
    }


    private String getPath(String patientId, String fileName) {
        return String.format("%s/%s/", FileUtil.generateFileName(fileName), patientId);
    }

    /**
     * 删除文件
     *
     * @param key 文件在七牛云中的key
     * @return 删除结果
     * @throws Exception 删除异常
     */
    public boolean deleteFile(String key) throws Exception {
        // 初始化七牛云存储管理器
        com.qiniu.storage.BucketManager bucketManager = new com.qiniu.storage.BucketManager(auth, new Configuration());
        Response response = bucketManager.delete(qiniuConfig.getBucket(), key);
        return response.isOK();
    }
}