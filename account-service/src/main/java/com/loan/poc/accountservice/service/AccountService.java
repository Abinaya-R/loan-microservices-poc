package com.loan.poc.accountservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.loan.poc.accountservice.dto.AccountResponse;
import com.loan.poc.accountservice.dto.CreateAccountRequest;
import com.loan.poc.accountservice.dto.CreditRequest;
import com.loan.poc.accountservice.dto.DebitCreditResponse;
import com.loan.poc.accountservice.dto.DebitRequest;
import com.loan.poc.accountservice.dto.UserValidationResponse;
import com.loan.poc.accountservice.feign.UserClient;
import com.loan.poc.accountservice.model.Account;
import com.loan.poc.accountservice.dto.AccountType;
import com.loan.poc.accountservice.dto.LoanStatus;
import com.loan.poc.accountservice.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserClient userClient;

    public ResponseEntity<AccountResponse> createAccount(CreateAccountRequest request) {
        if (request == null
                || request.getUserId() == null
                || request.getAccountType() == null
                || request.getInitialDeposit() == null) {
            return ResponseEntity.badRequest().build();
        }

        UserValidationResponse userVal =
            userClient.validateUser(SecurityContextHolder.getContext()
                    .getAuthentication().getName()).getBody();

    if (!userVal.isValid()) {
        throw new RuntimeException("Invalid user - User does not exist in User Service");
    }

        Account account = Account.builder()
                .userId(request.getUserId())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .status(request.getAccountType() == AccountType.LOAN ? LoanStatus.NEW : LoanStatus.ACTIVE)
                .build();
        Account saved = accountRepository.save(account);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    private AccountResponse mapToResponse(Account saved) {
        return AccountResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .accountType(saved.getAccountType())
                .balance(saved.getBalance())
                .status(saved.getStatus())
                .build();
    }

    public ResponseEntity<AccountResponse> getAccountById(Long id) {
    return accountRepository.findById(id)
        .map(acc -> ResponseEntity.ok(mapToResponse(acc)))
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(AccountResponse.builder().status(LoanStatus.ACCOUNT_NOT_FOUND).build()));
    }

    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(Long userId) {
        List<AccountResponse> accounts = accountRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(java.util.Collections.singletonList(
                AccountResponse.builder().status(LoanStatus.ACCOUNT_NOT_FOUND).build()));
        }
        return ResponseEntity.ok(accounts);
    }

    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(java.util.Collections.singletonList(
                AccountResponse.builder().status(LoanStatus.ACCOUNT_NOT_FOUND).build()));
        }
        return ResponseEntity.ok(accounts);
    }

    
    /**
     * Debit money from deposit OR loan repayment.
     * Returns DebitCreditResponse instead of plain String.
     */
    public ResponseEntity<DebitCreditResponse> debit(DebitRequest request) {

        Account acc = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (acc.getAccountType() == AccountType.DEPOSIT) {
            // Deposit account → deduct normally
            if (acc.getBalance().compareTo(request.getAmount()) < 0) {
                return ResponseEntity.badRequest()
                    .body(DebitCreditResponse.builder()
                        .message("Insufficient balance")
                        .success(false)
                        .build());
            }

            acc.setBalance(acc.getBalance().subtract(request.getAmount()));
        }

        else if (acc.getAccountType() == AccountType.LOAN) {
            // Loan payment received → reduce outstanding principal
            BigDecimal newOutstanding = acc.getBalance().subtract(request.getAmount());

            if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
                acc.setBalance(BigDecimal.ZERO);
                acc.setStatus(LoanStatus.CLOSED); // AUTO CLOSE LOAN
            } else {
                acc.setBalance(newOutstanding);
                acc.setStatus(LoanStatus.ACTIVE);
            }
        }

        accountRepository.save(acc);

        return ResponseEntity.ok(DebitCreditResponse.builder()
                .message("Amount debited successfully")
                .success(true)
                .build());
    }

    /**
     * Credit deposit accounts only.
     * Returns DebitCreditResponse instead of plain String.
     */
    public ResponseEntity<DebitCreditResponse> credit(CreditRequest request) {

        Account acc = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (acc.getAccountType() != AccountType.DEPOSIT) {
            return ResponseEntity.badRequest()
                .body(DebitCreditResponse.builder()
                    .message("Cannot credit a loan account")
                    .success(false)
                    .build());
        }

        acc.setBalance(acc.getBalance().add(request.getAmount()));
        accountRepository.save(acc);

        return ResponseEntity.ok(DebitCreditResponse.builder()
                .message("Amount credited successfully")
                .success(true)
                .build());
    }
}

