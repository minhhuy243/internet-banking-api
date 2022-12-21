package com.internetbanking.repository;

import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByRecipientAccountIdOrAccountIdAndStatus(Long recipientAccountId, Long accountId, TransactionStatus status, Pageable pageable);
}
