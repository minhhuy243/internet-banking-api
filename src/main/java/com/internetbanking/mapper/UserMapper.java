package com.internetbanking.mapper;

import com.internetbanking.dto.UserDto;
import com.internetbanking.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AccountMapper accountMapper;

    public UserDto entityToDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .birthday(entity.getBirthday())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .role(entity.getRole().getCode())
                .account(accountMapper.entityToDto(entity.getAccount()))
                .build();
    }
}
