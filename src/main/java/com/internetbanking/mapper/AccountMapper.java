package com.internetbanking.mapper;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.entity.Account;
import com.internetbanking.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final SecurityService securityService;

    public AccountDto entityToDto(Account entity) {
        if (entity == null) {
            return null;
        }
        return AccountDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .accountNumber(Long.valueOf(entity.getAccountNumber()))
                .dateOpened(entity.getDateOpened())
                .balance(entity.getBalance())
                .type(entity.getType())
                .email(entity.getUser().getEmail())
                .fullName(entity.getUser().getFullName())
                .phoneNumber(entity.getUser().getPhoneNumber())
                .address(entity.getUser().getAddress())
                .birthday(entity.getUser().getBirthday())
                .role(entity.getUser().getRole().getCode())
                .build();
    }
}
