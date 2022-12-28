package com.newagesol.mini_proj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.io.IOException;


@Configuration
@ActiveProfiles("test")
public class TestAwsConfig {

    @Bean
    public LocalStackContainer localStackContainer() {
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.11.3");

        LocalStackContainer localstack = new LocalStackContainer(localstackImage)
                .withServices(LocalStackContainer.Service.DYNAMODB);
        localstack.start();

        return localstack;
    }

    @Bean
    @Primary
    public DynamoDbClient dynamoDbClientConfig(LocalStackContainer localstack) {
        DynamoDbClient build = DynamoDbClient
                .builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .region(Region.of(localstack.getRegion()))
                .build();
        initTable(build);
        return build;
    }


    private void initTable(DynamoDbClient dynamoDbClient) {
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("id")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(10L).writeCapacityUnits(10L)
                        .build())
                .tableName("customers")
                .build();

        dynamoDbClient.createTable(request);
    }
}
