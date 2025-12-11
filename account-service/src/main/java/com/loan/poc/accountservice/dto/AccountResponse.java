package com.loan.poc.accountservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

import com.loan.poc.accountservice.model.AccountType;
import com.loan.poc.accountservice.model.LoanStatus;

@Data
@Builder
public class AccountResponse {

    private Long id;

    private Long userId;

    private AccountType accountType;

    private BigDecimal balance;

    private LoanStatus status;
}
