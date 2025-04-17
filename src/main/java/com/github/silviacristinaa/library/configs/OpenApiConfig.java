package com.github.silviacristinaa.library.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library")
                        .description("Library management service")
                        .contact(new Contact()
                                .name("Silvia Cristina Alexandre")
                                .url("https://www.linkedin.com/in/silvia-cristina-alexandre")
                                .email("silviacristinaalexandre1@gmail.com"))
                        .version("v1"));
    }
}
