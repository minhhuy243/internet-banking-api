package com.internetbanking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.internetbanking.entity.type.DebtReminderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DebtReminderDto {
    private Long id;
    private BigDecimal amount;
    private String content;
    private Boolean active;
    private String cancellationReason;
    private DebtReminderStatus status;
    private LocalDateTime createdAt;
    private Long debtAccountNumber;
    private Long accountNumber;
}
