package com.vention.agroex.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("openapi/swagger-config.yaml")) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            OpenAPI openAPI = objectMapper.readValue(inputStream, OpenAPI.class);
            log.info("Successfully read swagger configuration");
            return openAPI;
        }
        catch (Exception e){
            log.error("Error during reading swagger configuration");
            log.error(e.getMessage());
        }
        return new OpenAPI();
    }
}
