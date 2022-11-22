package com.internetbanking.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class TransactionType extends JpaEntity {

    @NotBlank
    private String code;

    @NotBlank
    private String description;
}
