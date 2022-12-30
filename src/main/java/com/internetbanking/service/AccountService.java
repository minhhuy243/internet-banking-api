package com.internetbanking.service;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import com.internetbanking.mapper.AccountMapper;
import com.internetbanking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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

    public Page<AccountDto> getAll(Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(pageable);
        return new PageImpl<>(
                accounts.getContent().stream().map(accountMapper::entityToDto).collect(Collectors.toList()),
                accounts.getPageable(),
                accounts.getTotalElements()
        );
    }

    public void delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException());
        accountRepository.delete(account);
    }
}
