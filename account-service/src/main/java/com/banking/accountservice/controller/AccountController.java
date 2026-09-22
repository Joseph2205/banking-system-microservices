package com.banking.accountservice.controller;

import com.banking.accountservice.dto.AccountResponse;
import com.banking.accountservice.dto.CreateAccountRequest;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@RequiredArgsConstructor

public class AccountController {

        private final AccountService accountService;

        @PostMapping
        public ResponseEntity<AccountResponse> createAccount (
                @valid @RequestBody CreateAccountRequest request){

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(accountService.createAccount(request));
        }

        @GetMapping("/{accountNumber}")
        public ResponseEntity<AccountResponse> getAccount (
                @PathVariable String accountNumber){

                return ResponseEntity.ok(accountService.getAccount(accountNumber))
        }

        @GetMapping("/{accountNumber}/balance")
        public ResponseEntity<BigDecimal> getBalance (
                @PathVariable String accountNumber){

                return ResponseEntity.ok(accountService.getBalance(accountNumber))
        }
         @PutMapping("/{accountNumber}/block")
        public ResponseEntity<String> blockAccount (
                @PathVariable String accountNumber){

                accountService.blockAccount(accountNumber);
                return ResponseEntity.ok("Account blocked Successfully");
        }

        /**
         * SAGA STEP 1 - DEDUCT BALANCE
         * CALLED BY TRANSACTION SERVICE WHEN TRANSFER IS INITIATED
         */
        @PutMapping("{accountNumber}/deduct")
        public ResponseEntity<String> deductBalance(
                @PathVariable String accountNumber,
                @RequestParam BigDecimal amount){
                accountService.deductBalance(accountNumber,amount);
                return ResponseEntity.ok("Balance deducted Successfully");
        }

        /**
         * SAGA STEP 4 - COMPENSATING TRANSACTION ENDPOINT
         * CALLED BY TRANSACTION SERVICE IN TWO SCENARIOS
         * 1. FRAUD DETECTED -> REFUND SENDER (UNDO STEP 1)
         * 2. TRANSACTION COMPLETED -> CREDIT RECEIVER
         */

        @PutMapping("/{accountNumber}/credit")
        public ResponseEntity<String> creditBalance (
                @PathVariable String accountNumber,
                @RequestParam BigDecimal amount){
                accountService.creditBalance(accountNumber,amount)
                        return ResponseEntity.ok("Balance credited Successfully");
        }


}


































