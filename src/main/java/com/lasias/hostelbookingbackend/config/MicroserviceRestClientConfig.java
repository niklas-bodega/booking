package com.lasias.hostelbookingbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MicroserviceRestClientConfig {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                .baseUrl(userServiceUrl)
                .defaultHeader("X-Internal-Call", "true")
                .build();
    }

}

