package com.abiz.model.dto;

import com.abiz.model.OperationType;
import lombok.AllArgsConstructor;
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
public class TransactionDto {

    @NotNull
    private String transferorAccountNumber;
    @NotNull
    private String transfereeAccountNumber;
    @Positive
    private Double amount;
    private String operatorName;
    private String description;

    @SuperBuilder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info extends TransactionDto {
        private Double balance;
        private Date transferDate;
        private String transferorAccountHolder;
        private String transfereeAccountHolder;
        private OperationType operationType;
    }

}
