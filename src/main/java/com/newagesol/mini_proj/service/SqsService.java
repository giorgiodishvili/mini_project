package com.newagesol.mini_proj.service;

import com.newagesol.mini_proj.annotations.SqsStream;
import com.newagesol.mini_proj.entity.Customer;
import com.newagesol.mini_proj.facade.SqsClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SqsService {

    private final SqsClientFacade<Customer> sqsClient;

    public SqsService(SqsClientFacade sqsClient) {
        this.sqsClient = sqsClient;
        sendBatchMessages();
    }

    public void sendBatchMessages() {

        System.out.println("\nSend multiple messages");
        Customer customer1 = new Customer("giorgi", "odisha.giorgi@gmail.com", LocalDateTime.now().toString());
        Customer customer2 = new Customer("revaza", "revaza.antona@gmail.com", LocalDateTime.now().toString());

        sqsClient.sendMessage(customer1, Customer.class);
        sqsClient.sendMessage(customer2, Customer.class);
    }

    @Scheduled(fixedDelayString = "${aws.sqs.fixed-poll-rate}")
    public void messageInBatchListener() {
        List<Customer> customerMessages = sqsClient.receiveMessages(Customer.class);

        log.info("Received {} message(s)", customerMessages.size());

        System.out.println("Message " + customerMessages);

//        if (!customerEntities.isEmpty()) {
//            log.info("Saving {} purchase transaction(s)", customerEntities.size());
//            purchaseTransactionRepository.saveAll(customerEntities);
//
//            List<Message> processed = messages.stream()
//                    .filter(m -> Boolean.parseBoolean(m.getAttributes().get("processed"))).toList();
//
//            deleteMessagesBatch(processed);
//
//            //processed.forEach(this::deleteMessage);
//        }
    }

}
