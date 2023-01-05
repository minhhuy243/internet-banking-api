package com.internetbanking.entity;

import lombok.*;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class BIDVAccount extends JpaEntity {
    private String accountNumber;
    private String fullName;
    private BigDecimal balance;

}
