package com.internetbanking.service;

import com.internetbanking.dto.DebtReminderDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.DebtReminder;
import com.internetbanking.entity.type.DebtReminderStatus;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.mapper.DebtReminderMapper;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.DebtReminderRepository;
import com.internetbanking.request.DebtReminderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtReminderService {

    private final DebtReminderRepository debtReminderRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;
    private final TransactionService transactionService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final DebtReminderMapper debtReminderMapper;

    public DebtReminderDto getById(Long debtReminderId) {
        return debtReminderMapper.entityToDto(
                debtReminderRepository.findById(debtReminderId).orElseThrow(() -> new RuntimeException("Nhắc nợ không tồn tại!"))
        );
    }

    public Page<DebtReminderDto> getDebtReminderList(Boolean createdByMyself, Pageable pageable) {
        Page<DebtReminder> debtReminders;
        if (createdByMyself != null) {
            if (createdByMyself == true) {
                debtReminders = debtReminderRepository.findByAccountIdAndActiveIsTrue(securityService.getAccountId(), pageable);
            } else {
                debtReminders = debtReminderRepository.findByDebtAccountIdAndActiveIsTrue(securityService.getAccountId(), pageable);
            }
        } else {
            debtReminders = debtReminderRepository.findByDebtAccountIdOrAccountIdAndActiveIsTrue(securityService.getAccountId(), securityService.getAccountId(), pageable);
        }

        return new PageImpl<>(
                debtReminders.getContent().stream().map(debtReminderMapper::entityToDto).collect(Collectors.toList()),
                debtReminders.getPageable(),
                debtReminders.getTotalElements()
        );
    }

    public DebtReminderDto create(DebtReminderRequest request) {
        Account debtAccount = accountRepository.findByAccountNumber(request.getDebtAccountNumber())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
        Account account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException());
        DebtReminder debtReminder = new DebtReminder().toBuilder()
                .content(request.getContent())
                .amount(request.getAmount())
                .status(DebtReminderStatus.UNPAID)
                .active(true)
                .debtAccount(debtAccount)
                .account(account)
                .build();
        return debtReminderMapper.entityToDto(debtReminderRepository.save(debtReminder));
    }

    public void cancelDebtReminder(Long debtReminderId, DebtReminderRequest request) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId).orElseThrow(() -> new RuntimeException("Nhắc nợ không tồn tại!"));
        if (!debtReminder.getActive()) {
            throw new RuntimeException("Nhắc nợ đã bị huỷ!");
        }
        if (debtReminder.getStatus() == DebtReminderStatus.PAID) {
            throw new RuntimeException("Nhắc nợ đã thanh toán!");
        }
        debtReminder.setActive(false);
        debtReminder.setCancellationReason(request.getCancellationReason());
        debtReminderRepository.save(debtReminder);
    }

    public void payment(Long debtReminderId) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId).orElseThrow(() -> new RuntimeException("Nhắc nợ không tồn tại!"));
        if (!debtReminder.getActive()) {
            throw new RuntimeException("Nhắc nợ đã bị huỷ!");
        }
        if (debtReminder.getStatus() == DebtReminderStatus.PAID) {
            throw new RuntimeException("Nhắc nợ đã thanh toán!");
        }
        Integer otpValue = otpService.generateOtp(debtReminder.getId());
        emailService.sendMessage(securityService.getEmail(), otpValue);
    }

    @Transactional
    public DebtReminderDto validatePayment(Integer otpValue) {
        Long debtReminderId = Long.valueOf(otpService.validateOTP(otpValue).toString());
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId).orElseThrow(() -> new RuntimeException("Nhắc nợ không tồn tại!"));
        if (!debtReminder.getActive()) {
            throw new RuntimeException("Nhắc nợ đã bị huỷ!");
        }
        debtReminder.setStatus(DebtReminderStatus.PAID);
        DebtReminder newDebtReminder = debtReminderRepository.save(debtReminder);
        transactionService.createFromDebtReminder(debtReminder);
        return debtReminderMapper.entityToDto(newDebtReminder);
    }
}
