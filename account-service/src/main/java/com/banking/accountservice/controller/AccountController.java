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

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@RequiredArgsConstructor

public class AccountController {

        private final AccountService accountService;

        @PostMapping
        public ResponseEntity<AccountResponse> createAccount (
                @valid_@RequestBody CreateAccountRequest request){

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(accountService.createAccount(request));
        }

        @GetMapping("/{accountNumber}")
        public ResponseEntity<AccountResponse> getAccount (
                @PathVariable String accountNumber){

                return ResponseEntity.ok(accountService.getAccount(accountNumber))
        }


}


































