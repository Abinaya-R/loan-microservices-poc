package com.loan.poc.paymentservice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LoanPaymentRequest {
    private Long loanAccountId;      // Loan account ID
    private Long depositAccountId;   // Deposit account used for payment
    private BigDecimal amount;           // EMI amount
    private String description;      // Payment description
}