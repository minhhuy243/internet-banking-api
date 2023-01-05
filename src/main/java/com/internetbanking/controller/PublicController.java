package com.internetbanking.controller;

import com.internetbanking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/public/BIDV")
@RequiredArgsConstructor
public class PublicController {

    @GetMapping("/{fromBankCode}/userInfo")
    public ResponseEntity<?> getUserInfo(String accountNumber) {
        return ResponseEntity.ok("MinBui");
    }

    @PostMapping("/{fromBankCode}/userBalance")
    public ResponseEntity<?> updateBalance(String accountNumber, BigDecimal balance) {
        return ResponseEntity.ok().build();
    }
}
