package com.banking.frauddetectionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionEventConsumer {

    private final FraudDetectionService fraudDetectionService;

    /**
     * LISTENS TO TRANSACTION.INITIATED TOPIC
     * EVERY TRANSACTION GOES THROUGH FRAUD CHECK BEFORE COMPLETING
     */

    @KafkaListener(topics = "transaction.initiated"
            ,groupId = "fraud-detection-group")

    public void consumeTransactionInitiated(
        @Payload Map<String,Object> payload){

        log.info("Received transaction for fraud check: {}",
              payload.get("transactionId"));
        try{
            fraudDetectionService.checkTransaction(payload);
        } catch (Exception e) {

        }
    }
}



















