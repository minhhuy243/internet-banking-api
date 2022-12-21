package com.internetbanking.entity;

import com.internetbanking.entity.type.DebtReminderStatus;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class DebtReminder extends JpaEntity {

    private BigDecimal amount;

    @Size(max = 100)
    private String content;

    private DebtReminderStatus status;

    private Boolean active;

    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account debtAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

}
