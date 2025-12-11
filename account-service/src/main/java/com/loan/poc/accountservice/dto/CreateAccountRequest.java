package com.loan.poc.accountservice.dto;

import java.math.BigDecimal;

import com.loan.poc.accountservice.model.AccountType;

import lombok.Data;

@Data
public class CreateAccountRequest {

    private Long userId;

    private AccountType accountType;

    private BigDecimal initialDeposit;

}