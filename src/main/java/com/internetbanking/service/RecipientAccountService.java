package com.internetbanking.service;

import com.internetbanking.entity.Account;
import com.internetbanking.entity.RecipientAccount;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.RecipientAccountRepository;
import com.internetbanking.request.RecipientAccountCreateRequest;
import com.internetbanking.request.RecipientAccountUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipientAccountService {

    private final RecipientAccountRepository recipientAccountRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    public List<RecipientAccount> getByAccountId() {
        return recipientAccountRepository.findByAccountId(securityService.getAccountId());
    }

    public RecipientAccount create(RecipientAccountCreateRequest request) {
        Account recipientAccount = accountRepository.findById(request.getRecipientAccountId()).orElseThrow(() -> new RuntimeException());
        RecipientAccount entity = new RecipientAccount()
                .toBuilder()
                .recipientAccount(recipientAccount)
                .reminiscentName(!request.getReminiscentName().isEmpty() ? request.getReminiscentName() : recipientAccount.getFullName())
                .account(accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException()))
                .build();
        return recipientAccountRepository.save(entity);
    }

    public RecipientAccount update(Long id, RecipientAccountUpdateRequest request) {
        RecipientAccount recipientAccount = recipientAccountRepository.findByIdAndAccountId(id, securityService.getAccountId())
                .orElseThrow(() -> new RuntimeException());
        recipientAccount.setReminiscentName(request.getReminiscentName());
        return recipientAccountRepository.save(recipientAccount);
    }

    public void delete(Long id) {
        RecipientAccount recipientAccount = recipientAccountRepository.findByIdAndAccountId(id, securityService.getAccountId())
                .orElseThrow(() -> new RuntimeException());
        recipientAccountRepository.delete(recipientAccount);
    }
}
