package com.loan.poc.paymentservice.repository;

import com.loan.poc.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByLoanAccountId(Long loanAccountId);
}
