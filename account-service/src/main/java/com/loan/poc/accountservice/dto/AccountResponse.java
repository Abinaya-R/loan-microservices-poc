package com.loan.poc.accountservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {

    private Long id;

    private Long userId;

    private String accountType;

    private BigDecimal balance;

    private String status;

    public Object map(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'map'");
    }
}
