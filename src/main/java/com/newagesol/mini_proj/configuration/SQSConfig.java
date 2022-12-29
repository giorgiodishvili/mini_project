package com.newagesol.mini_proj.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Configuration
public class SQSConfig {

    @Bean
    public SqsAsyncClient amazonSQSClient(AWSConfigProperties awsConfigProperties) {
        return SqsAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsConfigProperties.getAccessKeyId(), awsConfigProperties.getSecretKey())))
                .region(Region.of(awsConfigProperties.getRegion()))
                .build();
    }
}