package com.internetbanking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.entity.type.TransactionType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
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

    @Size(max = 100)
    private String content;

    private Boolean internal = true;

    private TransactionType type;

    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account recipientAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
}
