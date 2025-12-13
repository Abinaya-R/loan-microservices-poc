package com.loan.poc.paymentservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DebitRequest {
    private Long accountId;
    private BigDecimal amount;
}
