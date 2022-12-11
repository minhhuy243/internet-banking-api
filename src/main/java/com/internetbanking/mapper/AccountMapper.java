package com.internetbanking.mapper;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    public AccountDto entityToDto(Account entity) {
        return AccountDto.builder()
                .fullName(entity.getUser().getFullName())
                .accountNumber(entity.getAccountNumber())
                .dateOpened(entity.getDateOpened())
                .balance(entity.getBalance())
                .type(entity.getType())
                .build();
    }
}
