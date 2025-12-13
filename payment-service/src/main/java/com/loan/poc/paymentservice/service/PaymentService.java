package com.loan.poc.paymentservice.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.loan.poc.paymentservice.dto.AccountResponse;
import com.loan.poc.paymentservice.dto.DebitCreditResponse;
import com.loan.poc.paymentservice.dto.DebitRequest;
import com.loan.poc.paymentservice.dto.LoanPaymentRequest;
import com.loan.poc.paymentservice.dto.PaymentResponse;
import com.loan.poc.paymentservice.feign.AccountClient;
import com.loan.poc.paymentservice.model.Payment;
import com.loan.poc.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

        @Autowired
        private final AccountClient accountClient;  // Feign client to Account MS

        @Autowired
        private PaymentRepository repo;
        /**
         * 1. Check deposit balance
         * 2. Debit deposit account
         * 3. Pay loan → update status
         */
        public ResponseEntity<String> payLoan(LoanPaymentRequest request) {

            // STEP 1 → Get accounts
            AccountResponse depositAcc = accountClient.getById(request.getDepositAccountId());
            AccountResponse loanAcc = accountClient.getById(request.getLoanAccountId());

            // Validation
            if (!depositAcc.getUserId().equals(loanAcc.getUserId())) {
                return ResponseEntity.badRequest().body("Deposit and Loan accounts belong to different users!");
            }

            if (depositAcc.getBalance().compareTo(request.getAmount()) < 0) {
                return ResponseEntity.badRequest().body("Insufficient deposit balance");
            }

            // create payment record as PENDING
            Payment p = new Payment();
            p.setDepositAccountId(request.getDepositAccountId());
            p.setLoanAccountId(request.getLoanAccountId());
            p.setUserId(loanAcc.getUserId());
            p.setAmount(request.getAmount());
            p.setTxType("PAYMENT");
            p.setStatus("PENDING");
            p.setTransactionId(UUID.randomUUID().toString());
            p.setDescription(request.getDescription());

            Payment saved = repo.save(p);

            // STEP 2 → Debit deposit account
            DebitRequest debitReq = new DebitRequest();
            debitReq.setAccountId(depositAcc.getId());
            debitReq.setAmount(request.getAmount());

            DebitCreditResponse depositResp;
            try {
                depositResp = accountClient.debit(debitReq);
            } catch (Exception ex) {
                saved.setStatus("FAILED");
                saved.setUpdatedAt(java.time.Instant.now());
                repo.save(saved);
                return ResponseEntity.status(502).body("Failed to debit deposit account");
            }

           

            if (!depositResp.isSuccess()) {
                saved.setStatus("FAILED");
                saved.setUpdatedAt(java.time.Instant.now());
                repo.save(saved);
                return ResponseEntity.badRequest().body("Deposit account debit was not successful");
            }

            // STEP 3 → Apply EMI to loan account
            DebitRequest loanDebit = new DebitRequest();
            loanDebit.setAccountId(loanAcc.getId());
            loanDebit.setAmount(request.getAmount());

            DebitCreditResponse loanResp;
            try {
                loanResp = accountClient.debit(loanDebit);
            } catch (Exception ex) {
                saved.setStatus("FAILED");
                saved.setUpdatedAt(java.time.Instant.now());
                repo.save(saved);
                return ResponseEntity.status(502).body("Failed to debit loan account");
            }

            

            if (depositResp.isSuccess() && loanResp.isSuccess()) {
                saved.setStatus("SUCCESS");
            } else {
                saved.setStatus("FAILED");
            }
            saved.setUpdatedAt(java.time.Instant.now());
            repo.save(saved);

            if (saved.getStatus().equals("SUCCESS")) {
                return ResponseEntity.ok("Loan EMI paid successfully");
            } else {
                return ResponseEntity.status(502).body("Loan payment failed");
            }
        }

     public PaymentResponse getPaymentById(Long id) {
        var p = repo.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        return toDto(p);
    }

    private PaymentResponse toDto(Payment p) {
        PaymentResponse r = new PaymentResponse();
        r.setId(p.getId());
        r.setLoanAccountId(p.getLoanAccountId());
        r.setUserId(p.getUserId());
        r.setAmount(p.getAmount());
        r.setTxType(p.getTxType());
        r.setStatus(p.getStatus());
        r.setTransactionId(p.getTransactionId());
        r.setDescription(p.getDescription());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}

