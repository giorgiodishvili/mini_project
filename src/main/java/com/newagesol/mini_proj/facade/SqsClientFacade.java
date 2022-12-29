package com.newagesol.mini_proj.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newagesol.mini_proj.configuration.AWSConfigProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Slf4j
public class SqsClientFacade<T> {

    private final SqsAsyncClient sqsClient;
    private final AWSConfigProperties awsConfigProperties;
    private final ObjectMapper mapper;

    public SqsClientFacade(SqsAsyncClient sqsClient, AWSConfigProperties awsConfigProperties, ObjectMapper mapper) {
        this.sqsClient = sqsClient;
        this.awsConfigProperties = awsConfigProperties;
        this.mapper = mapper;
    }

    @SneakyThrows
    public List<T> receiveMessages(Class<T> expectedClass) {

        List<Message> messages = sqsClient.receiveMessage(constructReceiveMessageRequest(expectedClass)).get().messages();

        Stream<Message> messageStream = awsConfigProperties.getSqs().parallelProcessing() ? messages.stream().parallel() : messages.stream();

        return messageStream
                .map(it -> deserializeMessage(it, expectedClass))
                .filter(Objects::nonNull)
                .toList();
    }

    public void sendMessage(T content, Class<T> expectedClass) {
        try {
            sqsClient.sendMessageBatch(constructSendMessageBatchRequest(content, expectedClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not parse message toString: ", e);
        }

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
