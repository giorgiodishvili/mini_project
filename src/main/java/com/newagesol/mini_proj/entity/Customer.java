package com.newagesol.mini_proj.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;

@ToString
@NoArgsConstructor //You need a empty default constructor
@Getter
@Setter
@DynamoDbBean
@AllArgsConstructor
public class Customer {

    private String id = UUID.randomUUID().toString();

    private String custName;

    private String email;

    private String registrationDate;

    public Customer(String custName, String email, String registrationDate) {
        this.custName = custName;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public String getCustName() {
        return custName;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
}