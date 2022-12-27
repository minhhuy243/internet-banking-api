package com.internetbanking.mapper;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final UserMapper userMapper;

    public AccountDto entityToDto(Account entity) {
        if (entity == null) {
            return null;
        }
        return AccountDto.builder()
                .accountNumber(entity.getAccountNumber())
                .dateOpened(entity.getDateOpened())
                .balance(entity.getBalance())
                .type(entity.getType())
                .user(userMapper.entityToDto(entity.getUser()))
                .build();
    }
}
