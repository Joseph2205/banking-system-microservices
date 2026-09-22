package com.banking.transactionservice.controller;

import com.banking.transactionservice.dto.TransactionResponse;
import com.banking.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
        @valid @RequestBody TransferRequest request){

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionService.transfer(request));
        }

    @GetMapping("/{transactionId}")
    public  ReponseEntity<TransactionResponse> getTransaction(
        @PathVariable String transactionId){

            return ResponseEntity.ok(transaction.getTransaction(transactionId));
        }
    @GetMapping("/account/{accountNumber}")
    public  ReponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber){

        return ResponseEntity.ok(transaction.getTransactionHistory(accountNumber));
    }

    @PostMapping("{transactionId}/verify")
    public ResponseEntity<TransactionResponse> verifyOTP(
            @PathVariable String TransactionId,
            @RequestParam String otp){

        log.info("OTP verification request - transaction:{}",transactionId);

        return ResponseEntity.ok(
                transactionService.verifyOTP(transcationId,otp));

    }


    }

