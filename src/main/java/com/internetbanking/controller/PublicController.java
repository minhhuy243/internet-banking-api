package com.internetbanking.controller;

import com.internetbanking.entity.BIDVAccount;
import com.internetbanking.repository.BIDVAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/public/BIDV")
@RequiredArgsConstructor
public class PublicController {

    private final BIDVAccountRepository bidvAccountRepository;

    @GetMapping("/{fromBankCode}/userInfo")
    public ResponseEntity<?> getUserInfo(String accountNumber) {
        BIDVAccount bidvAccount = bidvAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        return ResponseEntity.ok(bidvAccount);
    }

    @PostMapping("/{fromBankCode}/updateBalance")
    public ResponseEntity<?> updateBalance(String accountNumber, BigDecimal balance) {
        BIDVAccount bidvAccount = bidvAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        bidvAccount.setBalance(bidvAccount.getBalance().add(balance));
        return ResponseEntity.ok(bidvAccountRepository.save(bidvAccount));
    }

    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(String accountNumber, String fullName, BigDecimal balance) {
        BIDVAccount bidvAccount = new BIDVAccount(accountNumber, fullName, balance);
        return ResponseEntity.ok(bidvAccountRepository.save(bidvAccount));
    }
}
