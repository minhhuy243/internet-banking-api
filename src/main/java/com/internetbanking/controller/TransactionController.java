package com.internetbanking.controller;

import com.internetbanking.entity.Transaction;
import com.internetbanking.request.TransactionRequest;
import com.internetbanking.service.OtpService;
import com.internetbanking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final OtpService otpService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.create(request);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(Integer otpValue) {
        Boolean result = otpService.validateOTP("minhhuy243@gmail.com", otpValue);
        return ResponseEntity.ok(result);
    }
}
