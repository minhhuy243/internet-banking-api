package com.internetbanking.mapper;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    public TransactionDto entityToDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .tradingDate(entity.getTradingDate())
                .amount(entity.getAmount())
                .content(entity.getContent())
                .internal(entity.getInternal())
                .type(entity.getType())
                .status(entity.getStatus())
                .recipientAccountNumber(entity.getRecipientAccount().getAccountNumber())
                .accountNumber(entity.getAccount().getAccountNumber())
                .build();
    }
}
