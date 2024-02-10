package com.vention.agroex.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vention.agroex.props.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final MinioProperties minioProperties;
    private final InputStream supportedExtensionsFile = getClass().getClassLoader().getResourceAsStream("minio/supported-extensions.json");

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public Map<String, String> supportedExtensions(ObjectMapper mapper) throws IOException {
        log.info("Reading supported file extensions from JSON file");
        var extensions = mapper.readValue(supportedExtensionsFile, new TypeReference<Map<String, String>>() {});
        log.info("Supported file extensions uploaded from JSON file");
        return extensions;
    }
}