package com.vention.agroex.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("aws")
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String userPoolId;
    private String clientId;
    private String clientSecret;
}
