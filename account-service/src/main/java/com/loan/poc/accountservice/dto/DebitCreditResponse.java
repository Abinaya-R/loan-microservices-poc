package com.loan.poc.accountservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitCreditResponse {
    private boolean success;
    private String message;
    private Object details; // optional payload
}
