package com.internetbanking.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipientAccountCreateRequest {
    private Long recipientAccountId;
    private String reminiscentName;
}
