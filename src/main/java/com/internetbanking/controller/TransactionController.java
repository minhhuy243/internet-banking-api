package com.internetbanking.controller;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionType;
import com.internetbanking.request.TransactionRequest;
import com.internetbanking.service.OtpService;
import com.internetbanking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getById(@PathVariable("transactionId") Long transactionId) {
        TransactionDto results = transactionService.getById(transactionId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("history") // thieu filter loai
    public ResponseEntity<?> getHistory(@PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                        @RequestParam(name = "type", required = false) String type,
                                        @RequestParam(name = "startFrom", required = false) Long startFrom,
                                        @RequestParam(name = "endFrom", required = false) Long endFrom) {
        Page<TransactionDto> results = transactionService.getHistory(type, startFrom, endFrom, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("history/{accountId}")
    public ResponseEntity<?> getHistoryByAccountId(@PathVariable("accountId") Long accountId,
                                                   @RequestParam(name = "type", required = false) String type,
                                                   @RequestParam(name = "startFrom", required = false) Long startFrom,
                                                   @RequestParam(name = "endFrom", required = false) Long endFrom,
                                            @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionDto> results = transactionService.getHistoryByAccountId(accountId, type, startFrom, endFrom, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody TransactionRequest request) throws MessagingException {
        Object result = transactionService.create(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate/{otpValue}")
    public ResponseEntity<?> validate(@PathVariable("otpValue") Integer otpValue) {
        Object result = transactionService.validate(otpValue);
        return ResponseEntity.ok(result);
    }

}
