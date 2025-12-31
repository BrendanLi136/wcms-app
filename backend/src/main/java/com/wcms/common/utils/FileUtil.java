package com.wcms.common.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtil {
    /**
     * 获取文件名后缀
     *
     * @param fileName 文件名
     * @return 文件名后缀
     */
    public static String getFileExtension(String fileName) {
        if (ObjectUtils.isEmpty(fileName)) {
            return "";
        }
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        // 转换为小写
        return fileName.substring(lastIndex + 1).toLowerCase();
    }

    /**
     * 生成随机文件名
     *
     * @param fileName 文件名
     * @return 随机文件名
     */
    public static String generateFileName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return UUID.randomUUID() + ".jpg";
        }

        return  UUID.randomUUID() + "." + getFileExtension(fileName);
    }
}
