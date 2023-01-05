package com.internetbanking.repository;

import com.internetbanking.entity.Transaction;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("FROM Transaction t WHERE (t.recipientAccount.id = ?1 OR t.account.id = ?2) AND t.status = ?3")
    Page<Transaction> findByRecipientAccountIdOrAccountIdAndStatus
            (Long recipientAccountId, Long accountId, TransactionStatus status, Pageable pageable);

    @Query("FROM Transaction t WHERE (t.recipientAccount.id = ?1 OR t.account.id = ?2) AND t.status = ?3 AND t.type = ?4")
    Page<Transaction> findByRecipientAccountIdOrAccountIdAndStatusAndType
            (Long recipientAccountId, Long accountId, TransactionStatus status, TransactionType type, Pageable pageable);
}
