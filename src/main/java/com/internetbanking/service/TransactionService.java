package com.internetbanking.service;

import com.internetbanking.dto.TransactionDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.DebtReminder;
import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.User;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import com.internetbanking.mapper.TransactionMapper;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.TransactionRepository;
import com.internetbanking.repository.UserRepository;
import com.internetbanking.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final TransactionMapper transactionMapper;

    public TransactionDto getById(Long transactionId) {
        return transactionMapper.entityToDto(transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại!")));
    }

    @Transactional
    public TransactionDto create(TransactionRequest request) throws MessagingException {
        if (request.getType() == TransactionType.TRANSFER) {
            Account recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber()).orElseThrow(() -> new RuntimeException("Tài khoản người nhận không tồn tại!"));
            Account account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException("Tài khoản người gửi không tồn tại!"));

            if (!account.isActive()) {
                throw new RuntimeException("Tài khoản đã bị khoá. Không thể thực hiện giao dịch!");
            }
            if (!recipientAccount.isActive()) {
                throw new RuntimeException("Tài khoản người nhận đã bị khoá. Không thể thực hiện giao dịch!");
            }
            if (request.getAmount().compareTo(account.getBalance()) > 0) {
                throw new RuntimeException("Tài khoản không đủ số dư!");
            }
            Transaction transaction = new Transaction().toBuilder()
                    .tradingDate(LocalDate.now().atStartOfDay())
                    .amount(request.getAmount())
                    .content(request.getContent())
                    .internal(request.getInternal())
                    .status(TransactionStatus.VERIFYING)
                    .balanceOfRecipientAccount(BigDecimal.ZERO)
                    .balanceOfAccount(BigDecimal.ZERO)
                    .type(request.getType())
                    .recipientAccount(recipientAccount)
                    .account(account)
                    .build();
            Transaction newTransaction = transactionRepository.saveAndFlush(transaction);

            Integer otpValue = otpService.generateOtp(newTransaction.getId());
            if (!emailService.sendMessage(securityService.getEmail(), otpValue, "TRANSACTION")) {
                newTransaction.setStatus(TransactionStatus.ERROR);
                transactionRepository.save(newTransaction);
            }
            return transactionMapper.entityToDto(newTransaction);
        } else if (request.getType() == TransactionType.DEPOSIT) {
            Account recipientAccount;
            if (request.getEmail() != null ) {
                User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Tên đăng nhập không tồn tại!"));
                recipientAccount = user.getAccount();
            } else {
                recipientAccount = accountRepository.findByAccountNumber(request.getRecipientAccountNumber()).orElseThrow(() -> new RuntimeException("Tài khoản người nhận không tồn tại!"));
            }
            Account account = accountRepository.findById(securityService.getAccountId()).orElseThrow(() -> new RuntimeException("Tài khoản người gửi không tồn tại!"));
            Transaction transaction = new Transaction().toBuilder()
                    .tradingDate(LocalDateTime.now())
                    .amount(request.getAmount())
                    .content(request.getContent())
                    .internal(request.getInternal())
                    .status(TransactionStatus.DONE)
                    .balanceOfAccount(BigDecimal.ZERO)
                    .balanceOfRecipientAccount(recipientAccount.getBalance().add(request.getAmount()))
                    .type(request.getType())
                    .recipientAccount(recipientAccount)
                    .account(account)
                    .build();
            recipientAccount.setBalance(recipientAccount.getBalance().add(transaction.getAmount()));
            return transactionMapper.entityToDto(transactionRepository.save(transaction));
        }

        return null;
    }

    @Transactional
    public TransactionDto validate(Integer otpValue) {
        Long transactionId = Long.valueOf(otpService.validateOTP(otpValue).toString());
        Transaction transaction = transactionRepository.findById(Long.valueOf(transactionId.toString())).orElseThrow(() -> new RuntimeException());
        Account recipientAccount = transaction.getRecipientAccount();
        Account account = transaction.getAccount();
        if (!account.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khoá. Không thể thực hiện giao dịch!");
        }
        if (!recipientAccount.isActive()) {
            throw new RuntimeException("Tài khoản người nhận đã bị khoá. Không thể thực hiện giao dịch!");
        }
        if (transaction.getStatus() == TransactionStatus.CANCELED) {
            throw new RuntimeException("Giao dịch đã bị huỷ!");
        }
        if (transaction.getAmount().compareTo(account.getBalance()) > 0) {
            transaction.setStatus(TransactionStatus.CANCELED);
            throw new RuntimeException("Tài khoản không đủ số dư!");
        }
        if (transaction.getType() == TransactionType.TRANSFER) {
            transaction.setStatus(TransactionStatus.DONE);
            transaction.setBalanceOfRecipientAccount(recipientAccount.getBalance().add(transaction.getAmount()));
            transaction.setBalanceOfAccount(account.getBalance().subtract(transaction.getAmount()));
            recipientAccount.setBalance(recipientAccount.getBalance().add(transaction.getAmount()));
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }
        return transactionMapper.entityToDto(transactionRepository.save(transaction));
    }

    public Page<TransactionDto> getHistory(String type, Long startFrom, Long endFrom, Pageable pageable) {
        Page<Transaction> transactions;
        Specification<Transaction> transactionSpecification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), TransactionStatus.DONE));

            if (type != null) {
                if (type.equalsIgnoreCase("transfer")) {
                    predicates.add(cb.equal(root.get("type"), TransactionType.TRANSFER));
                    predicates.add(cb.equal(root.get("account").get("id"), securityService.getAccountId()));
                }
                if (type.equalsIgnoreCase("receive")) {
                    predicates.add(cb.equal(root.get("recipientAccount").get("id"), securityService.getAccountId()));
                }
            }
            if (startFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tradingDate"), Instant.ofEpochMilli(startFrom).atZone(ZoneOffset.UTC).toLocalDateTime()));
            }
            if (endFrom != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tradingDate"), Instant.ofEpochMilli(endFrom).atZone(ZoneOffset.UTC).toLocalDateTime()));
            }
            Predicate predicateAnd = cb.and(predicates.toArray(new Predicate[0]));
            Predicate predicateOr = null;
            if (type == null || type.isEmpty()) {
                predicateOr = cb.or(cb.equal(root.get("account").get("id"), securityService.getAccountId()),
                        cb.equal(root.get("recipientAccount").get("id"), securityService.getAccountId()));
            }

            if (predicateOr == null) {
                return predicateAnd;
            }
            return cb.and(predicateAnd, predicateOr);
        };
        transactions = transactionRepository.findAll(transactionSpecification, pageable);
        return new PageImpl<>(
                transactions.getContent().stream().map(transactionMapper::entityToDto).collect(Collectors.toList()),
                transactions.getPageable(),
                transactions.getTotalElements()
        );
    }

    public Page<TransactionDto> getHistoryByAccountId(Long accountId, String type, Long startFrom, Long endFrom, Pageable pageable) {
        Page<Transaction> transactions;
        Specification<Transaction> transactionSpecification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (type != null) {
                if (type.equalsIgnoreCase("transfer")) {
                    predicates.add(cb.equal(root.get("type"), TransactionType.TRANSFER));
                    predicates.add(cb.equal(root.get("account").get("id"), accountId));
                }
                if (type.equalsIgnoreCase("receive")) {
                    predicates.add(cb.equal(root.get("recipientAccount").get("id"), accountId));
                }
            }
            if (startFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("tradingDate"), Instant.ofEpochMilli(startFrom).atZone(ZoneOffset.UTC).toLocalDateTime()));
            }
            if (endFrom != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("tradingDate"), Instant.ofEpochMilli(endFrom).atZone(ZoneOffset.UTC).toLocalDateTime()));
            }
            Predicate predicateAnd = cb.and(predicates.toArray(new Predicate[0]));
            Predicate predicateOr = null;
            if (type == null || type.isEmpty()) {
                predicateOr = cb.or(cb.equal(root.get("account").get("id"), accountId),
                        cb.equal(root.get("recipientAccount").get("id"), accountId));
            }

            if (predicateOr == null) {
                return predicateAnd;
            }
            return cb.and(predicateAnd, predicateOr);
        };
        transactions = transactionRepository.findAll(transactionSpecification, pageable);

        return new PageImpl<>(
                transactions.getContent().stream().map(transactionMapper::entityToDto).collect(Collectors.toList()),
                transactions.getPageable(),
                transactions.getTotalElements()
        );
    }

    public TransactionDto createFromDebtReminder(DebtReminder debtReminder) {
        Account recipientAccount = debtReminder.getAccount();
        Account account = debtReminder.getDebtAccount();
        if (!account.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khoá. Không thể thực hiện giao dịch!");
        }
        if (!recipientAccount.isActive()) {
            throw new RuntimeException("Tài khoản người nhận đã bị khoá. Không thể thực hiện giao dịch!");
        }
        if (debtReminder.getAmount().compareTo(account.getBalance()) > 0) {
            throw new RuntimeException("Tài khoản không đủ số dư!");
        }
        Transaction transaction = new Transaction().toBuilder()
                .tradingDate(LocalDateTime.now())
                .amount(debtReminder.getAmount())
                .content(debtReminder.getContent())
                .internal(true)
                .status(TransactionStatus.DONE)
                .type(TransactionType.TRANSFER)
                .balanceOfRecipientAccount(recipientAccount.getBalance().add(debtReminder.getAmount()))
                .balanceOfAccount(account.getBalance().subtract(debtReminder.getAmount()))
                .recipientAccount(recipientAccount)
                .account(account)
                .build();
        recipientAccount.setBalance(recipientAccount.getBalance().add(transaction.getAmount()));
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        return transactionMapper.entityToDto(transactionRepository.save(transaction));
    }
}
