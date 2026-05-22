package com.sk.iba.config;

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
    public OpenAPI ibaAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI行为分析系统")
                        .description("AI行为分析系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("zzy")
                        )
                );
    }
}