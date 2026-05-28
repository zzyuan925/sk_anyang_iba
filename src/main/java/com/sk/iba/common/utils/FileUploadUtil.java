package com.sk.iba.common.utils;

import com.sk.iba.common.config.FileUploadProperties;
import com.sk.iba.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 文件上传工具
 *
 * @author zzy
 */
@Component
@RequiredArgsConstructor
public class FileUploadUtil {

    private final FileUploadProperties fileUploadProperties;

    /**
     * 上传算法包
     *
     * @param file 算法包文件
     * @param functionCode 功能代号
     * @param version 版本号
     * @return 文件存储路径
     */
    public String uploadAlgorithmPackage(MultipartFile file, String functionCode, String version) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("算法包文件不能为空");
        }
        if (!StringUtils.hasText(fileUploadProperties.getAlgorithmPackageDir())) {
            throw new BusinessException("算法包上传目录未配置");
        }
        if (!StringUtils.hasText(functionCode)) {
            throw new BusinessException("算法功能代号不能为空");
        }
        if (!StringUtils.hasText(version)) {
            throw new BusinessException("算法包版本不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException("算法包文件名不能为空");
        }

        originalFilename = StringUtils.cleanPath(originalFilename);
        if (originalFilename.contains("..")) {
            throw new BusinessException("算法包文件名不合法");
        }

        String safeFunctionCode = safePathPart(functionCode);
        String safeVersion = safePathPart(version);

        try {
            Path baseDir = Path.of(fileUploadProperties.getAlgorithmPackageDir())
                    .toAbsolutePath()
                    .normalize();

            Path targetDir = baseDir
                    .resolve(safeFunctionCode)
                    .resolve(safeVersion)
                    .normalize();

            if (!targetDir.startsWith(baseDir)) {
                throw new BusinessException("算法包存储路径不合法");
            }

            Files.createDirectories(targetDir);

            Path targetFile = targetDir.resolve(originalFilename).normalize();
            if (!targetFile.startsWith(targetDir)) {
                throw new BusinessException("算法包文件路径不合法");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return targetFile.toString();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("算法包上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件，失败不抛异常
     */
    public void deleteQuietly(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }

        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (Exception ignored) {
        }
    }

    /**
     * 防止 functionCode / version 里带 / \ .. 这种路径字符
     */
    private String safePathPart(String value) {
        String cleanValue = StringUtils.cleanPath(value.trim());

        if (cleanValue.contains("..") || cleanValue.contains("/") || cleanValue.contains("\\")) {
            throw new BusinessException("文件夹名称不合法");
        }

        return cleanValue;
    }
}