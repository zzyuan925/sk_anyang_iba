package com.sk.taskflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzy
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI taskFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow 企业协作工单系统接口文档")
                        .description("TaskFlow 后端接口文档，包含用户、权限、团队、项目、工单、通知等模块")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("zzy")
                        )
                );
    }
}