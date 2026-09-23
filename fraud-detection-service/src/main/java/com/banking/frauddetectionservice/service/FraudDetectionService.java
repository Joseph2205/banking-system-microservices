package com.banking.frauddetectionservice.service;

import com.banking.frauddetectionservice.client.AccountServiceClient;
import com.banking.frauddetectionservice.model.FraudCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String VERIFICATION_REQUIRED_TOPIC = "verification.required";
    private static final String FRAUD_CHECK_RESULT_TOPIC = "fraud.check.clean";


    public void checkTransaction(Map<String,Object> payload){

        String transactionId = (String) payload.get("transactionId");
        String accountNumber = (String) payload.get("senderAccountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());

        //FETCH REAL BALANCE FROM ACCOUNT SERVICE

        BigDecimal senderBalance = accountServiceClient.getBalance(accountNumber);

        log.info("Checking transaction: {} account: {} amount: {} balance: {}",
                transactionId, accountNumber, amount, senderBalance);

       FraudCheckResult result =  performFraudCheck(accountNumber,amount,senderBalance);

       if(result.isFraud()){

           log.info("Suspicious activity detected - account: {} " +
                   "reason: {} - requestion OTP verification",
                   accountNumber, result.getReason());

           Map<String,Object> verificationEvent = new HashMap<>();
           verificationEvent.put("transactionId",transactionId);
           verificationEvent.put("accountNumber",accountNumber);
           verificationEvent.put("amount",amount);
           verificationEvent.put("reason",result.getReason());

           kafkaTemplate.send(VERIFICATION_REQUIRED_TOPIC, transactionId, verificationEvent);

       }
       else{
           //   TRANSACTION IS CLEAN
           log.info("Transaction clean");

           Map<String,Object> transactionCleanEvent = new HashMap<>();
           transactionCleanEvent.put("transactionId",transactionId);
           transactionCleanEvent.put("isFraud",false);
           transactionCleanEvent.put("reason",null);

           kafkaTemplate.send(FRAUD_CHECK_RESULT_TOPIC, transactionId, transactionCleanEvent);
       }


    }
}
