package com.fintech.pagamentos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API de Pagamentos - Fintech")
                .version("1.0.0")
                .description("Documentação da API de gerenciamento de clientes, faturas e pagamentos")
                .contact(new Contact().name("Cauany Rodrigues").email("Cauanynunes00@gmail.com")));
    }
}