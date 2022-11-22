package com.internetbanking.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class RecipientAccount extends JpaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Account recipientAccount;

    @NotBlank
    private String reminiscentName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
}
