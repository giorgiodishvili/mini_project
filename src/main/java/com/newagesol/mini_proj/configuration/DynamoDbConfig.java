package com.newagesol.mini_proj.configuration;

import com.newagesol.mini_proj.entity.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(AWSConfigProperties awsConfigProperties) {
        return DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsConfigProperties.getAccessKeyId(), awsConfigProperties.getSecretKey())))
                .region(Region.of(awsConfigProperties.getRegion()))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<Customer> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                                 AWSConfigProperties awsConfigProperties) {
        return dynamoDbEnhancedClient.table(awsConfigProperties.getDynamoDb().get(Customer.class.getSimpleName().toLowerCase()), TableSchema.fromBean(Customer.class));
    }


    // we can use this for custom mapping
//    @Bean
//    public TableSchema<Customer> customerTableSchema() {
//        return
//                TableSchema.builder(Customer.class)
//                        .newItemSupplier(Customer::new)
//                        .addAttribute(String.class, a -> a.name("id")
//                                .getter(Customer::getId)
//                                .setter(Customer::setId)
//                                .tags(primaryPartitionKey()))
//                        .addAttribute(String.class, a -> a.name("email")
//                                .getter(Customer::getEmail)
//                                .setter(Customer::setEmail)
//                                .tags(primarySortKey()))
//                        .addAttribute(String.class, a -> a.name("custName")
//                                .getter(Customer::getCustName)
//                                .setter(Customer::setCustName))
//                        .addAttribute(String.class, a -> a.name("registrationDate")
//                                .getter(Customer::getRegistrationDate)
//                                .setter(Customer::setRegistrationDate))
//                        .build();
//    }
}
