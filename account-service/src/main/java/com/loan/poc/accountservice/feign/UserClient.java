package com.loan.poc.accountservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.loan.poc.accountservice.dto.UserValidationResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/validate/{username}")
    public ResponseEntity<UserValidationResponse> validateUser(@PathVariable String username);
}
