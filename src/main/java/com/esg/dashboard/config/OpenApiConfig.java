package com.esg.dashboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:esg-dashboard}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Configuring OpenAPI documentation for {}", applicationName);

        return new OpenAPI()
                .info(new Info()
                        .title("ESG Dashboard API")
                        .description("Real-time ESG monitoring and analytics platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ESG Dashboard Team")
                                .email("support@esg-dashboard.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.esg-dashboard.com")
                                .description("Production Server")
                ));
    }
}