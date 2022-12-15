package com.internetbanking.service;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import com.internetbanking.mapper.TransactionMapper;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.TransactionRepository;
import com.internetbanking.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDto create(TransactionRequest request) {
        Account recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber()).orElseThrow(() -> new RuntimeException("Tài khoản người nhận không tồn tại!"));
        Account account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException("Tài khoản người gửi không tồn tại!"));
        if (request.getAmount().compareTo(account.getBalance()) > 0) {
            throw new RuntimeException("Tài khoản không đủ số dư!");
        }
        Transaction transaction = new Transaction().toBuilder()
                .tradingDate(LocalDateTime.now())
                .amount(request.getAmount())
                .content(request.getContent())
                .internal(request.getInternal())
                .status(TransactionStatus.VERIFYING)
                .type(request.getType())
                .recipientAccount(recipientAccount)
                .account(account)
                .build();
        Transaction newTransaction = transactionRepository.saveAndFlush(transaction);

        Integer otpValue = otpService.generateOtp(newTransaction.getId());
        if (!emailService.sendMessage(securityService.getEmail(), otpValue)) {
            newTransaction.setStatus(TransactionStatus.ERROR);
            transactionRepository.save(newTransaction);
        }
        return transactionMapper.entityToDto(newTransaction);
    }

    @Transactional
    public TransactionDto validate(Integer otpValue) {
        Optional<Object> transactionId = Optional.ofNullable(otpService.validateOTP(otpValue));
        if (!transactionId.isPresent()) {
            throw new RuntimeException("OTP không hợp lệ!");
        }
        Transaction transaction = transactionRepository.findById(Long.valueOf(transactionId.get().toString())).orElseThrow(() -> new RuntimeException());
        transaction.setStatus(TransactionStatus.DONE);
        Account recipientAccount = transaction.getRecipientAccount();
        Account account = transaction.getAccount();
        if (transaction.getAmount().compareTo(account.getBalance()) > 0) {
            throw new RuntimeException("Tài khoản không đủ số dư!");
        }
        if (transaction.getType() == TransactionType.TRANSFER) {
            recipientAccount.setBalance(recipientAccount.getBalance().add(transaction.getAmount()));
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
        return transactionMapper.entityToDto(transactionRepository.save(transaction));
    }

    public List<TransactionDto> getHistory(Pageable pageable) {
        Page<Transaction> transactions
                = transactionRepository.findByRecipientAccountIdOrAccountId(securityService.getAccountId(), securityService.getAccountId(), pageable);
        if (transactions.hasContent()) {
            return transactions.getContent().stream().map(transactionMapper::entityToDto).collect(Collectors.toList());
        }
        return null;
    }
}
