package com.internetbanking.service;

import com.internetbanking.entity.Account;
import com.internetbanking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    public Account getInfo() {
        return accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException());
    }
}
