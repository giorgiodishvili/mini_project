package com.newagesol.mini_proj.repository;

import com.newagesol.mini_proj.entity.Customer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DynamoDbService {

    private final DynamoDbTable<Customer> customerTable;

    public DynamoDbService(DynamoDbTable<Customer> customerTable) {
        this.customerTable = customerTable;
    }

    @PostConstruct
    public void writeData() {
        Customer giorgi = new Customer("giorgi", "odishvili.giorgi@bla.com", LocalDateTime.now().toString());
        save(giorgi);
        log.info("get User Giorgi: " + findById(giorgi.getId()).toString());
        log.info("get ALL Customers: " + findAll());

    }

    public void save(Customer customer) {
        customerTable.putItem(customer);
    }

    public Customer findById(String id) {
        return customerTable.getItem(Key.builder().partitionValue(id).build());
    }

    public List<Customer> findAll() {
        return customerTable.scan().items().stream().toList();
    }
}
