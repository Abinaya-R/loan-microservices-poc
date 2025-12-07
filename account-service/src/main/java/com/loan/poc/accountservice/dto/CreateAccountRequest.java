package com.loan.poc.accountservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateAccountRequest {

    private Long userId;

    private String accountType;

    private BigDecimal initialDeposit;
}