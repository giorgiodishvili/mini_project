package com.newagesol.mini_proj.service;

import com.newagesol.mini_proj.entity.Customer;
import com.newagesol.mini_proj.entity.Player;
import com.newagesol.mini_proj.facade.SqsClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.utils.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class SqsService {

    private final SqsClientFacade<Customer> sqsClient;
    private final SqsClientFacade<Player> playerSqsClient;

    public SqsService(SqsClientFacade sqsClient, SqsClientFacade<Player> playerSqsClient) {
        this.sqsClient = sqsClient;
        this.playerSqsClient = playerSqsClient;
        sendBatchMessages();
    }

    public void sendBatchMessages() {

        System.out.println("\nSend multiple messages");
        Customer customer1 = new Customer("giorgi", "odisha.giorgi@gmail.com", LocalDateTime.now().toString());
        Customer customer2 = new Customer("revaza", "revaza.antona@gmail.com", LocalDateTime.now().toString());

        sqsClient.sendMessage(customer1, Customer.class);
        sqsClient.sendMessage(customer2, Customer.class);

        playerSqsClient.sendMessage(new Player(), Player.class);
    }

    @Scheduled(fixedDelayString = "${aws.sqs.fixed-poll-rate}")
    public void messageInBatchListener() {
        Function<Pair<Message, Customer>, Message> function = it -> it.left();

        sqsClient.receiveMessages(Customer.class, function);
//
//        log.info("Received {} message(s)", customerMessages.size());
////        sadasda
//        System.out.println("Message " + customerMessages);
//        System.out.println("Message  PLAYER " + playerSqsClient.receiveMessages(Player.class));
    }


}
