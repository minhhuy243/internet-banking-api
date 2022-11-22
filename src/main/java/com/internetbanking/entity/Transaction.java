package com.internetbanking.entity;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Transaction extends JpaEntity {

    private LocalDateTime tradingDate;

    private BigDecimal amount;

    private Boolean internal = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
}
