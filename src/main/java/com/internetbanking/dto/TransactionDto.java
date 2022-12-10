package com.internetbanking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransactionDto {

    private Long id;

    private LocalDateTime tradingDate;

    private BigDecimal amount;

    private String content;

    private Boolean internal;

    private TransactionType type;

    private TransactionStatus status;

    private Long recipientAccountNumber;

    private Long accountNumber;
}
