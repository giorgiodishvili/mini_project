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
@AllArgsConstructor
public class Player {

    private String id = UUID.randomUUID().toString();

    public String getId() {
        return id;
    }

}