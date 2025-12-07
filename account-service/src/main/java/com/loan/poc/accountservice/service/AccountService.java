package com.loan.poc.accountservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.loan.poc.accountservice.dto.AccountResponse;
import com.loan.poc.accountservice.dto.CreateAccountRequest;
import com.loan.poc.accountservice.exception.ResourceNotFoundException;
import com.loan.poc.accountservice.model.Account;
import com.loan.poc.accountservice.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<AccountResponse> createAccount(CreateAccountRequest request) {
        if (request == null
                || request.getUserId() == null
                || request.getAccountType() == null
                || request.getInitialDeposit() == null) {
            return ResponseEntity.badRequest().build();
        }

        Account account = Account.builder()
                .userId(request.getUserId())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .status("ACTIVE")
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
            .body(AccountResponse.builder().status("accounts not found").build()));
    }

    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(Long userId) {
        List<AccountResponse> accounts = accountRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(java.util.Collections.singletonList(
                AccountResponse.builder().status("accounts not found").build()));
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
                AccountResponse.builder().status("accounts not found").build()));
        }
        return ResponseEntity.ok(accounts);
    }

}
