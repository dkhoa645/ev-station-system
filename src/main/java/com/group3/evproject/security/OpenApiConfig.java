package com.group3.evproject.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EV Project API")
                        .version("1.0")
                        .description("API documentation for EV Project"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"))
                .addServersItem(new Server().url("https://ev-station-system-production.up.railway.app").description("Production server"));
    }
}