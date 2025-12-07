package com.loan.poc.accountservice.controller;

import com.loan.poc.accountservice.dto.CreateAccountRequest;
import com.loan.poc.accountservice.dto.AccountResponse;
import com.loan.poc.accountservice.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getByUserId(@PathVariable Long userId) {
        return accountService.getAccountsByUserId(userId);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAll() {
        return accountService.getAllAccounts();
    }
}
