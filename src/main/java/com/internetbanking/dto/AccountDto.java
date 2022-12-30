package com.internetbanking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.internetbanking.entity.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccountDto {
    private Long id;
    private Long accountNumber;
    private LocalDateTime dateOpened;
    private BigDecimal balance;
    private AccountType type;

    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate birthday;
}
