package com.internetbanking.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class BIDVLinkedBank extends JpaEntity {
    private String bankCode;

    private String linkedCode;

}
