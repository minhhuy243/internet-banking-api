package com.internetbanking.repository;

import com.internetbanking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByRecipientAccountIdOrAccountId(Long recipientAccountId, Long accountId, Pageable pageable);
}
