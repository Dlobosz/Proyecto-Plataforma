package com.diegolobos.plataforma.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente HTTP hacia Judge0. Soporta dos proveedores sin cambiar codigo,
 * solo config (ver application-local.properties.example):
 * - Self-hosted (por defecto): solo requiere judge0.api.url y judge0.api.auth-token.
 * - Judge0 CE via RapidAPI: requiere ademas judge0.api.key y judge0.api.host.
 * Los headers que no aplican a un proveedor simplemente no se envian.
 */
@Configuration
public class Judge0Config {

    @Value("${judge0.api.url:http://localhost:2358}")
    private String apiUrl;

    @Value("${judge0.api.key:}")
    private String apiKey;

    @Value("${judge0.api.host:}")
    private String apiHost;

    @Value("${judge0.api.auth-token:}")
    private String authToken;

    @Bean
    public WebClient judge0WebClient() {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json");

        if (!apiKey.isBlank()) {
            builder.defaultHeader("X-RapidAPI-Key", apiKey);
        }
        if (!apiHost.isBlank()) {
            builder.defaultHeader("X-RapidAPI-Host", apiHost);
        }
        if (!authToken.isBlank()) {
            // Header AUTHN_HEADER configurado en judge0/judge0.conf (default X-Auth-Token).
            builder.defaultHeader("X-Auth-Token", authToken);
        }
        return builder.build();
    }
}
