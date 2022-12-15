package com.internetbanking.controller;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Transaction;
import com.internetbanking.request.TransactionRequest;
import com.internetbanking.service.OtpService;
import com.internetbanking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("history")
    public ResponseEntity<?> getHistory(@PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        List<TransactionDto> results = transactionService.getHistory(pageable);
        return ResponseEntity.ok(results);
    }

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
