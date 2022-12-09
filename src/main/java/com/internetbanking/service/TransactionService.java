package com.internetbanking.service;

import com.internetbanking.entity.Account;
import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionStatus;
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
    public Transaction create(TransactionRequest request) {
//        Account recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber()).orElseThrow(() -> new RuntimeException());
        Transaction transaction = new Transaction().toBuilder()
                .tradingDate(LocalDateTime.now())
                .amount(request.getAmount())
                .content(request.getContent())
                .internal(request.getInternal())
                .status(TransactionStatus.VERIFYING)
                .type(request.getType())
//                .recipientAccount(recipientAccount)
//                .account(accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException()))
                .build();
        transactionRepository.save(transaction);

//        Integer otpValue = otpService.generateOtp(securityService.getAccountId().toString());
        Integer otpValue = otpService.generateOtp("minhhuy243@gmail.com");
        //if (!emailService.sendMessage(securityService.getUsername(), otpValue)) {
        if (!emailService.sendMessage("minhhuy243@gmail.com", otpValue)) {
            transaction.setStatus(TransactionStatus.ERROR);
            transactionRepository.save(transaction);
        }
        return transaction;
    }
}
