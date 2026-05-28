package com.sk.iba.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 *
 * @author zzy
 */
@Data
@Component
@ConfigurationProperties(prefix = "sk.file")
public class FileUploadProperties {

    /**
     * 算法包上传根目录
     */
    private String algorithmPackageDir;
}