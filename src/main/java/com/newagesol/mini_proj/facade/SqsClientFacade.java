package com.newagesol.mini_proj.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newagesol.mini_proj.configuration.AWSConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.utils.Pair;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Slf4j
public class SqsClientFacade<T> {

    private final SqsClient sqsClient;
    private final AWSConfigProperties awsConfigProperties;
    private final ObjectMapper mapper;

    public SqsClientFacade(SqsClient sqsClient, AWSConfigProperties awsConfigProperties, ObjectMapper mapper) {
        this.sqsClient = sqsClient;
        this.awsConfigProperties = awsConfigProperties;
        this.mapper = mapper;
    }

    public void receiveMessages(Class<T> expectedClass, Function<Pair<Message, T>, Message> function) {

        List<Message> messages = sqsClient.receiveMessage(constructReceiveMessageRequest(expectedClass)).messages();
        Stream<Message> messageStream = awsConfigProperties.getSqs().parallelProcessing() ? messages.stream().parallel() : messages.stream();

        messageStream
                .map(it -> Pair.of(it, Objects.requireNonNull(deserializeMessage(it, expectedClass))))
                .peek(it -> log.info("Received class: {} message: {} ", expectedClass.getSimpleName(), it.right()))
                .map(function)
                .filter(Objects::nonNull)
                .forEach(it -> deleteMessage(expectedClass, it.receiptHandle()));
    }

    public boolean sendMessage(T content, Class<T> expectedClass) {
        try {
            return sqsClient.sendMessageBatch(constructSendMessageBatchRequest(content, expectedClass)).hasSuccessful();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse message toString: ", e);
        }

    }

    public void deleteMessage(Class<T> expectedClass, String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(awsConfigProperties.getSqs().urls().get(expectedClass.getSimpleName().toLowerCase()))
                .receiptHandle(receiptHandle)
                .build());
    }

    private T deserializeMessage(Message message, Class<T> expectedClass) {
        log.trace("Deserializing message with id {}", message.messageId());
        try {
            String body = message.body();
            return mapper.readValue(body, expectedClass);
        } catch (Exception ex) {
            log.error(
                    "An error occurred during message deSerialization. Returning a message to a queue. Error details: \n<{}>",
                    ex.getLocalizedMessage());
            return null;
        }
    }

    private Message accept(Message message, Function<T, Message> function, Class<T> expectedClass) {
        T t = deserializeMessage(message, expectedClass);
        return function.apply(t);
    }

    private ReceiveMessageRequest constructReceiveMessageRequest(Class<T> expectedClass) {

        return ReceiveMessageRequest.builder()
                .queueUrl(awsConfigProperties.getSqs().urls().get(expectedClass.getSimpleName().toLowerCase()))
                .maxNumberOfMessages(awsConfigProperties.getSqs().batchSize())
                .waitTimeSeconds(awsConfigProperties.getSqs().pollWaitTimeSec())
                .build();
    }

    private SendMessageBatchRequest constructSendMessageBatchRequest(T content, Class<T> expectedClass) throws JsonProcessingException {
        return SendMessageBatchRequest.builder()
                .queueUrl(awsConfigProperties.getSqs().urls().get(expectedClass.getSimpleName().toLowerCase()))
                .entries(SendMessageBatchRequestEntry.builder().id(UUID.randomUUID().toString()).messageBody(mapper.writeValueAsString(content)).build())
                .build();
    }
}
