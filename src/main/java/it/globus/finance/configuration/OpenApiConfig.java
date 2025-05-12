package it.globus.finance.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Globus Finance API")
                        .version("1.0")
                        .description("""
                                Документация REST API проекта Globus Finance
                                                                
                                Telegram Contacts:
                                @whysobluebunny
                                @andrew_kir
                                @w3bpr0g3r
                                @AlexeyMuraviev
                                """));
    }
}
