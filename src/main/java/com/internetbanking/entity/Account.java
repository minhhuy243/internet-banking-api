package com.internetbanking.entity;

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

    @NotBlank(message = "{user.full-name.not-blank}")
    @Size(min = 3, max = 50, message = "{user.full-name.size}")
    private String fullName;

    private Long accountNumber;

    private LocalDateTime dateOpened;

    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<RecipientAccount> recipientAccounts = new ArrayList<>();
}
