package com.internetbanking.service;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.User;
import com.internetbanking.mapper.AccountMapper;
import com.internetbanking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public AccountDto getByAccountNumber(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber.toString()).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
        if (!account.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khoá");
        }
        return accountMapper.entityToDto(account);
    }

    public Page<AccountDto> getAll(Map<String, String> allParams, Pageable pageable) {
        Specification<Account> accountSpec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (securityService.getRole().equals("ROLE_EMPLOYEE")) {
                Join<User, Account> userAccountJoin = root.join("user");
                predicates.add(cb.equal(userAccountJoin.get("role").get("code"), "ROLE_CUSTOMER"));
            }

            if (allParams.containsKey("accountNumber")) {
                predicates.add(cb.like(root.get("accountNumber"), "%" + allParams.get("accountNumber") + "%"));
            }
            if (allParams.containsKey("type")) { // role = ROLE_ADMIN
                Join<User, Account> userAccountJoin = root.join("user");
                predicates.add(cb.equal(userAccountJoin.get("role").get("code"), allParams.get("type")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Account> accounts = accountRepository.findAll(accountSpec, pageable);
        return new PageImpl<>(
                accounts.getContent().stream().map(accountMapper::entityToDto).collect(Collectors.toList()),
                accounts.getPageable(),
                accounts.getTotalElements()
        );
    }

    public void delete(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        accountRepository.delete(account);
    }

    public void lock(Long id) {
        Account account;
        if (id == null) {
            account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        } else {
            account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        }
        account.setActive(false);
        accountRepository.save(account);
    }

    public void unlock(Long id) {
        Account account;
        if (id == null) {
            account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        } else {
            account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        }
        account.setActive(true);
        accountRepository.save(account);
    }
}
