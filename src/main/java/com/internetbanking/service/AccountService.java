package com.internetbanking.service;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import com.internetbanking.mapper.AccountMapper;
import com.internetbanking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final AccountMapper accountMapper;

    public AccountDto getInfo() {
        Account account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException());
        return accountMapper.entityToDto(account);
    }
}
