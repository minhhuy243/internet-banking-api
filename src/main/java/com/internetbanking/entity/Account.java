package com.internetbanking.entity;

import com.internetbanking.entity.type.AccountType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Account extends JpaEntity {

    private Long accountNumber;

    private LocalDateTime dateOpened;

    private BigDecimal balance;

    private AccountType type;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<RecipientAccount> recipientAccounts = new ArrayList<>();
}
