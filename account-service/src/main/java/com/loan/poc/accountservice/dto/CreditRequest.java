package com.loan.poc.accountservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreditRequest {
    private Long accountId;
    private BigDecimal amount;
}
