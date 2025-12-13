package com.loan.poc.paymentservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

/**
 * Response DTO for returning payment details.
 */
@Data
public class PaymentResponse {
    private Long id;
    private Long userId;
    private Long loanAccountId;
    private BigDecimal amount;
    private String txType;
    private String status;
    private String transactionId;
    private String description;
    private Instant createdAt;
}

