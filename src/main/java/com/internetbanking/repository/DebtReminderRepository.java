package com.internetbanking.repository;

import com.internetbanking.entity.DebtReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, Long> {

    Page<DebtReminder> findByDebtAccountIdOrAccountIdAndActiveIsTrue(Long debtAccountId, Long accountId, Pageable pageable);
    Page<DebtReminder> findByAccountIdAndActiveIsTrue(Long accountId, Pageable pageable);
    Page<DebtReminder> findByDebtAccountIdAndActiveIsTrue(Long debtAccountId, Pageable pageable);
}
