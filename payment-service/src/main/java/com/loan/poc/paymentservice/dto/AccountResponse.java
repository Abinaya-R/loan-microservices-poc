package com.loan.poc.paymentservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountResponse {
    private Long id;
    private Long userId;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal loanAmount;
    private LoanStatus status;
}

