package com.internetbanking.service;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.TransactionRepository;
import com.internetbanking.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final OtpService otpService;
    private final EmailService emailService;

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
        if (!emailService.sendMessage(securityService.getUsername(), otpValue)) {
            newTransaction.setStatus(TransactionStatus.ERROR);
            transactionRepository.save(newTransaction);
        }
        return TransactionDto.builder()
                .id(newTransaction.getId())
                .tradingDate(newTransaction.getTradingDate())
                .amount(newTransaction.getAmount())
                .content(newTransaction.getContent())
                .internal(newTransaction.getInternal())
                .type(newTransaction.getType())
                .status(newTransaction.getStatus())
                .recipientAccountNumber(newTransaction.getRecipientAccount().getAccountNumber())
                .accountNumber(newTransaction.getAccount().getAccountNumber())
                .build();
    }

    @Transactional
    public TransactionDto validate(Integer otpValue) {
        Long transactionId = Long.valueOf(otpService.validateOTP(otpValue).toString());
        if (transactionId == null) {
            throw new RuntimeException("OTP không hợp lệ!");
        }
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException());
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
        Transaction newTransaction = transactionRepository.save(transaction);
        return TransactionDto.builder()
                .id(newTransaction.getId())
                .tradingDate(newTransaction.getTradingDate())
                .amount(newTransaction.getAmount())
                .content(newTransaction.getContent())
                .internal(newTransaction.getInternal())
                .type(newTransaction.getType())
                .status(newTransaction.getStatus())
                .recipientAccountNumber(newTransaction.getRecipientAccount().getAccountNumber())
                .accountNumber(newTransaction.getAccount().getAccountNumber())
                .build();
    }
}
