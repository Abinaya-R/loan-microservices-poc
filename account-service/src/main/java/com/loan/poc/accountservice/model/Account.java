package com.loan.poc.accountservice.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.loan.poc.accountservice.dto.AccountType;
import com.loan.poc.accountservice.dto.LoanStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // linked to User microservice

    /**
     * accountType: "DEPOSIT" or "LOAN"
     * - DEPOSIT account: holds user's cash balance (can be used to pay)
     * - LOAN account: holds outstanding principal (positive number means amount
     * owed)
     */
    @Enumerated(EnumType.STRING)
    private AccountType accountType; 

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

     private Double loanAmount;  // For LOAN accounts only

    @Enumerated(EnumType.STRING)
    private LoanStatus status; // NEW, ACTIVE, CLOSED

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
