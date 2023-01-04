package com.abiz.model.dto;

import com.abiz.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @Positive
    private double balance;
    @NotNull
    private String accountHolder;
    private AccountType accountType;
    private boolean blocked;

    @SuperBuilder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info extends AccountDto {
        private Date createDate;
        private String accountNumber;
    }


}
