package com.loan.poc.paymentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.loan.poc.paymentservice.config.FeignConfig;
import com.loan.poc.paymentservice.dto.AccountResponse;
import com.loan.poc.paymentservice.dto.DebitCreditResponse;
import com.loan.poc.paymentservice.dto.DebitRequest;

@FeignClient(name = "account-service", url = "http://localhost:8092", configuration = FeignConfig.class)

public interface AccountClient {

    @GetMapping("/accounts/{id}")
    AccountResponse getById(@PathVariable Long id);

    @PostMapping("/accounts/debit")
    DebitCreditResponse debit(@RequestBody DebitRequest request);

    @PostMapping("/accounts/credit")
    DebitCreditResponse credit(@RequestBody DebitRequest request);
}
