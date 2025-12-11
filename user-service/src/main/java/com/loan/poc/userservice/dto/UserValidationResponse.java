package com.loan.poc.userservice.dto;

import lombok.Data;

/**
 * Response received from User Service after validating JWT username
 */
@Data
public class UserValidationResponse {
    private boolean valid;
    private Long userId;
}