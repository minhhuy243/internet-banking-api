package com.internetbanking.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "BIDV_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class BIDVAccount extends JpaEntity {
    @Column(unique = true)
    private String accountNumber;
    private String fullName;
    private BigDecimal balance;

}
