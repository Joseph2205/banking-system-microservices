package com.banking.accountservice.service;

import com.banking.accountservice.dto.AccountResponse;
import com.banking.accountservice.dto.CreateAccountRequest;
import com.banking.accountservice.entity.Account;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;
import com.banking.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private static SecureRandom secureRandom = new SecureRandom();

    public AcccountResponse createAccount(CreateAccountRequest request){
        log.info("Creating Account for :{}",request.getEmail());

        if(accountRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Account already exists for email:" + request.getEmail());
        }

        Account account = new Account();
        account.setAccountHolderName(request.getAccountHolderName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setAccountType(request.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(request.getInitialDeposit());
        account.setAccountNumber(generateAccountNumber());
        account.setDailyTransactionLimit(

                request.getAccountType()== AccountType.SAVINGS
                ? new BigDecimal("100000")
                        : new BigDecimal("500000")

        );
        Account savedAccount = accountRepository.save(account);
        log.info("Saved Account for :{}",savedAccount.getAccountNumber());
        return mapToResponse(savedAccount);
    }

    /**
     * GET ACCOUNT BY ACCOUNT NUMBER
     * @param accountNumber
     * @return
     */
    public AccountResponse getAccount(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber);
        .orElseThrow(() -> new RuntimeException("Account not found"));

        return mapToResponse(account);
    }

    /**
     * GET ACCOUNT BALANCE
     * @param accountNumber
     * @return
     */
    public BigDecimal getBalance(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber);
        .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }
/*
  BLOCK ACCOUNT - CALLED BY FRAUD DETECTION SERVICE VIA KAFKA
 */
    public void blockAccount(String accountNumber){
        log.info("Blocking Account for :{}",accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber);
        .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.info(" Account Blocked :{}",accountNumber);

    }

    /**
     * DEDUCT BALANCE FROM SENDER ACCOUNT
     * CALLED BY TRANSACTION SERVICE
     * @param accountNumber
     * @param amount
     */
    public void deductBalance(String accountNumber,BigDecimal amount){
        log.info("Deducting balance for :{} from account:{} ",amount,accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        .orElseThrow(() -> new RuntimeException("Account not found"));

        if(account.getStatus()!= AccountStatus.ACTIVE){
            throw new RuntimeException("Account status is not ACTIVE");
        }

        if(account.getBalance().compareTo(amount)<0){
            throw new RuntimeException("Insufficient funds for account balance"+accountNumber);
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        log.info("Balance updated.New Balance :{}",account.getBalance());


    }

    /**
     * CREDIT BALANCE
     * CALLED BY TRANSACTION SERVICE VIA KAFKA
     * @param accountNumber
     * @param amount
     */
    public void creditBalance(String accountNumber,BigDecimal amount){
        log.info("Crediting {} to account {}",amount,accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        log.info("Balance Credited.New Balance :{}",account.getBalance());
    }

//GENERATE UNIQUE 12 DIGIT ACCOUNTNUMBER
    private String generateAccountNumber(){

        String accountNumber;

        do{
            long number = secureRandom.nextLong(1_000_000_000_000L);
            
            accountNumber = String.format("%012d",number);

        }while(accountRepository.existsByAccountNUmber(accountNumber));

        return accountNumber;


    }

    private AccountResponse mapToResponse(Account account){
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountHolderName(account.getAccountHolderName());
        response.setEmail(account.getEmail());
        response.setPhone(account.getPhone());
        response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());

        return response;

    }


}





















































