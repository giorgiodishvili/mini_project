package com.newagesol.mini_proj.repository;

import com.newagesol.mini_proj.entity.Customer;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DynamoDbService {

    private final DynamoDbTemplate dynamoDbTemplate;

    public DynamoDbService(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    @PostConstruct
    public void writeData() {
        Customer giorgi = new Customer("giorgi", "odishvili.giorgi@bla.com", LocalDateTime.now().toString());
        save(giorgi);
        log.info("get User Giorgi: " + findById(giorgi.getId()).toString());
        log.info("get ALL Customers: " + findAll());

    }

    public void save(Customer customer) {
        dynamoDbTemplate.save(customer);
    }

    public Customer findById(String id) {
        return dynamoDbTemplate.load(Key.builder().partitionValue(id).build(), Customer.class);
    }

    public List<Customer> findAll() {
        return dynamoDbTemplate.scan(ScanEnhancedRequest.builder().limit(100).build(), Customer.class).items().stream().toList();
    }
}
