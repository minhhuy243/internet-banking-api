package com.internetbanking.repository;

import com.internetbanking.entity.BIDVAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BIDVAccountRepository extends JpaRepository<BIDVAccount, Long> {
    Optional<BIDVAccount> findByAccountNumber(String accountNumber);
}
