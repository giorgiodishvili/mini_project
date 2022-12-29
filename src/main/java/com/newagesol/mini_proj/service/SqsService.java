package com.newagesol.mini_proj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jashmore.sqs.argument.payload.Payload;
import com.jashmore.sqs.spring.container.basic.QueueListener;
import com.newagesol.mini_proj.entity.Customer;
import com.newagesol.mini_proj.facade.SqsClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SqsService {

    private final SqsClientFacade<Customer> sqsClient;
    private final ObjectMapper mapper;

    public SqsService(SqsClientFacade sqsClient, ObjectMapper mapper) {
        this.sqsClient = sqsClient;
        this.mapper = mapper;
        sendBatchMessages();
    }

    public void sendBatchMessages() {

        System.out.println("\nSend multiple messages");
        Customer customer1 = new Customer("giorgi", "odisha.giorgi@gmail.com", LocalDateTime.now().toString());
        Customer customer2 = new Customer("revaza", "revaza.antona@gmail.com", LocalDateTime.now().toString());

        sqsClient.sendMessage(customer1, Customer.class);
        sqsClient.sendMessage(customer2, Customer.class);
    }

    @QueueListener(value = "${aws.sqs.urls.customer}")
    public void messageInBatchListener(@Payload final Customer customerMessages) {
        System.out.println("Message " + customerMessages);
    }

}
