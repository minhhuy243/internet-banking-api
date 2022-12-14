package com.internetbanking.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebtReminderRequest {
    private BigDecimal amount;
    private String content;
    private String cancellationReason;
    private String debtAccountNumber;
}
