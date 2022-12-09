package com.internetbanking.entity;

import com.internetbanking.entity.type.DebtReminderStatus;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class DebtReminder extends JpaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Account debtAccount;

    @NotBlank
    private String reminiscentName;

    private BigDecimal amount;

    private DebtReminderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

}
