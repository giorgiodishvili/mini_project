package com.newagesol.mini_proj;

import com.newagesol.mini_proj.entity.Customer;
import com.newagesol.mini_proj.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j

//@Import(AwsConfig.class)
class MiniProjApplicationTests {
    //
    @Autowired
    private DynamoDbService dynamoDbService;


    @Test
    public void sampleTestCase() {
        Customer gosling = new Customer("gosling", "gosling@gmail.com", LocalDateTime.now().toString());
        dynamoDbService.save(gosling);

        Customer result = dynamoDbService.findById(gosling.getId());
        assertEquals(gosling, result);
        log.info("Found in table: {}", result);
    }

    public void assertEquals(Customer expected, Customer actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
        Assertions.assertEquals(expected.getRegistrationDate(), actual.getRegistrationDate());
        Assertions.assertEquals(expected.getCustName(), actual.getCustName());
    }
}
