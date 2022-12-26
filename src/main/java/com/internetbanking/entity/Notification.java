package com.internetbanking.entity;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Notification extends JpaEntity {

    private String title;
    private String content;
    private Boolean status;
}
