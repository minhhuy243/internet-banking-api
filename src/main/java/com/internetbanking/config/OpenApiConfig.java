package com.internetbanking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI getOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Internet Banking")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Nhom 4")
                                .url("https://www.hcmus.edu.vn/")
                        )
                );
    }
}
