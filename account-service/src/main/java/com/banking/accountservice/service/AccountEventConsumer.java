package com.banking.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountEventConsumer {
    private final AccountService accountService;


    /**
     * CONSUME TRANSACTION COMPLETED EVENT FROM KAFKA
     * CREDITS RECEIVER ACCOUNT
     * @param payload
     */
    @KafkaListener(topic = "transaction.completed")
    public void consumeTransactionCompleted(
            @Payload Map<String,Object> payload){

        try{String receiverAccount = (String) payload.get("receiverAccountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            log.info("Crediting account: {} amount: {}", receiverAccount, amount);
             accountService.creditBalance(receiverAccount,amount);

        }
        catch (Exception e){
            log.error("Error while credit account: {}",e.getMessage());
        }

    }

    /**
     * CONSUME FRAUD DETECTED EVVENT FROM KAFKA
     * BLOCK THE FLAGGED ACCOUNT
     * @param payload
     */
    @KafkaListener(topics = "fraud.dected")
    public void consumeFraudDetected(@Payload Map<String,Object> payload){
        try{

            String accountNumber = (String)payload.get("accountNumber);
                    log.info("Fraud detected - blocking account:{}",accountNumber);

            accountService.blockAccount(accountNumber);

        }
        catch (Exception e){
            log.error("Error while blocking account: {}",e.getMessage());
        }
    }
}
