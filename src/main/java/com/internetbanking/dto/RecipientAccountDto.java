package com.internetbanking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecipientAccountDto {
    private Long id;
    private Long recipientAccountNumber;
    private String recipientAccountName;
    private String reminiscentName;
}
