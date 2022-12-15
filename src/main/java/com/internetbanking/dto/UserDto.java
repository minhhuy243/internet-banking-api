package com.internetbanking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private LocalDate birthday;
    private String phoneNumber;
    private String address;
    private String role;
    private AccountDto account;
}
