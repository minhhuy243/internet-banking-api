package com.internetbanking.request;

import com.internetbanking.entity.type.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String email;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private String content;
    private Boolean internal;
    private TransactionType type;
}
