package com.internetbanking.repository;

import com.internetbanking.entity.RecipientAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipientAccountRepository extends JpaRepository<RecipientAccount, Long> {

    Optional<RecipientAccount> findByIdAndAccountId(Long id, Long accountId);
    Optional<RecipientAccount> findByRecipientAccount_AccountNumberAndAccount_Id(String accountNumber, Long id);
    List<RecipientAccount> findByAccountId(Long accountId);
}
