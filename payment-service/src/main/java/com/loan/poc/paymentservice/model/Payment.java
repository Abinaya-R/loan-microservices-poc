package com.loan.poc.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @Column(name = "user_id", nullable = false)
    private Long userId;     // owner of both accounts

    @Column(name = "deposit_account_id", nullable = false)
    private Long depositAccountId; // deposit account debited

    @Column(name = "loan_account_id", nullable = false)
    private Long loanAccountId;    // loan account credited (outstanding reduced)

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

     @Column(name = "tx_type", length = 20, nullable = false)
    private String txType;   // EMI, PAYMENT, REFUND

    @Column(nullable = false, length = 20)
    private String status;   // PENDING, SUCCESS, FAILED

    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}
