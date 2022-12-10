package com.internetbanking.controller;

import com.internetbanking.entity.Transaction;
import com.internetbanking.request.TransactionRequest;
import com.internetbanking.service.OtpService;
import com.internetbanking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody TransactionRequest request) {
        Object result = transactionService.create(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate/{otpValue}")
    public ResponseEntity<?> validate(@PathVariable("otpValue") Integer otpValue) {
        Object result = transactionService.validate(otpValue);
        return ResponseEntity.ok(result);
    }
}
