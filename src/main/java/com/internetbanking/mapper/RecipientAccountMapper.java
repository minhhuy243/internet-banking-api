package com.internetbanking.mapper;

import com.internetbanking.dto.RecipientAccountDto;
import com.internetbanking.entity.RecipientAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecipientAccountMapper {

    public RecipientAccountDto entityToDto(RecipientAccount entity) {
        return RecipientAccountDto.builder()
                .id(entity.getId())
                .recipientAccountNumber(Long.valueOf(entity.getRecipientAccount().getAccountNumber()))
                .recipientAccountName(entity.getRecipientAccount().getUser().getFullName())
                .reminiscentName(entity.getReminiscentName())
                .build();
    }
}
