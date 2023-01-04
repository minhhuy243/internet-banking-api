package com.internetbanking.service;

import com.internetbanking.dto.RecipientAccountDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.RecipientAccount;
import com.internetbanking.mapper.RecipientAccountMapper;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.RecipientAccountRepository;
import com.internetbanking.request.RecipientAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipientAccountService {

    private final RecipientAccountRepository recipientAccountRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final RecipientAccountMapper recipientAccountMapper;

    public Page<RecipientAccountDto> getByAccountId(Pageable pageable) {
        Page<RecipientAccount> recipientAccounts = recipientAccountRepository.findByAccountId(securityService.getAccountId(), pageable);
        return new PageImpl<>(
                recipientAccounts.getContent().stream().map(recipientAccountMapper::entityToDto).collect(Collectors.toList()),
                recipientAccounts.getPageable(),
                recipientAccounts.getTotalElements()
        );
    }

    public RecipientAccountDto create(RecipientAccountRequest request) {
        Account myAccount = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException());
        if (request.getRecipientAccountNumber().equals(myAccount.getAccountNumber())) {
            throw new RuntimeException("Không thể thêm chính bạn vào danh sách người nhận!");
        }

        Optional<RecipientAccount> recipientAccountEntity
                = recipientAccountRepository.findByRecipientAccount_AccountNumberAndAccount_Id(request.getRecipientAccountNumber(), securityService.getAccountId());
        if (recipientAccountEntity.isPresent()) {
            throw new RuntimeException("Tài khoản này đã tồn tại trong danh sách!");
        }

        Account recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        RecipientAccount entity = new RecipientAccount()
                .toBuilder()
                .recipientAccount(recipientAccount)
                .reminiscentName(!request.getReminiscentName().isEmpty() ? request.getReminiscentName() : recipientAccount.getUser().getFullName())
                .account(accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException()))
                .build();
        RecipientAccount newEntity = recipientAccountRepository.save(entity);
        return recipientAccountMapper.entityToDto(newEntity);
    }

    public RecipientAccountDto update(Long id, RecipientAccountRequest request) {
        RecipientAccount recipientAccount = recipientAccountRepository.findByIdAndAccountId(id, securityService.getAccountId())
                .orElseThrow(() -> new RuntimeException("Đã có lỗi xảy ra!"));
        recipientAccount.setReminiscentName(request.getReminiscentName());
        return recipientAccountMapper.entityToDto(recipientAccountRepository.save(recipientAccount));
    }

    public void delete(Long id) {
        RecipientAccount recipientAccount = recipientAccountRepository.findByIdAndAccountId(id, securityService.getAccountId())
                .orElseThrow(() -> new RuntimeException("Đã có lỗi xảy ra!"));
        recipientAccountRepository.delete(recipientAccount);
    }
}
