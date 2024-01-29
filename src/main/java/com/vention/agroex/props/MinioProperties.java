package com.vention.agroex.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String url;
}
