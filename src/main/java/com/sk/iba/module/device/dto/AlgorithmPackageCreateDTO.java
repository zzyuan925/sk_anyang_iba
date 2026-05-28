package com.sk.iba.module.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 新增算法包参数
 *
 * @author zzy
 */
@Schema(description = "新增算法包参数")
@Data
public class AlgorithmPackageCreateDTO {

    @Schema(description = "算法功能ID")
    @NotNull(message = "算法功能ID不能为空")
    private Long functionId;

    @Schema(description = "算法包版本")
    @NotBlank(message = "算法包版本不能为空")
    @Size(max = 50, message = "算法包版本不能超过50个字符")
    private String version;

    @Schema(description = "算法包描述")
    @Size(max = 1000, message = "算法包描述不能超过1000个字符")
    private String description;

    @Schema(description = "启动环境")
    @Size(max = 100, message = "启动环境不能超过100个字符")
    private String runtimeEnv;

    @Schema(description = "启动文件名")
    @NotBlank(message = "启动文件名不能为空")
    @Size(max = 255, message = "启动文件名不能超过255个字符")
    private String startFileName;

    @Schema(description = "权重文件路径")
    @Size(max = 500, message = "权重路径不能超过500个字符")
    private String weightPath;

    @Schema(description = "算法包文件")
    @NotNull(message = "算法包文件不能为空")
    private MultipartFile file;
}