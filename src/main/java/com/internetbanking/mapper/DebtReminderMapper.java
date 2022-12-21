package com.internetbanking.mapper;

import com.internetbanking.dto.DebtReminderDto;
import com.internetbanking.entity.DebtReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DebtReminderMapper {

    public DebtReminderDto entityToDto(DebtReminder entity) {
        return DebtReminderDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .content(entity.getContent())
                .active(entity.getActive())
                .cancellationReason(entity.getCancellationReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .debtAccountNumber(entity.getDebtAccount().getAccountNumber())
                .accountNumber(entity.getAccount().getAccountNumber())
                .build();
    }
}
