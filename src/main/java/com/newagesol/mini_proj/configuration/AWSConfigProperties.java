package com.newagesol.mini_proj.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AWSConfigProperties {
    private String bucket;
    private String accessKeyId;
    private String secretKey;
    private String region;
    private Map<String, String> dynamoDb;
}
