package com.abiz.mapper;

import com.abiz.model.Account;
import com.abiz.model.FinanceTransaction;
import com.abiz.model.dto.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {

    @Mapping( source = "transferorAccount", target = "transferorAccountHolder" , qualifiedByName = "accountHolder")
    @Mapping( source = "transfereeAccount", target = "transfereeAccountHolder" , qualifiedByName = "accountHolder")
    @Mapping( source = "transferorAccount", target = "transferorAccountNumber" , qualifiedByName = "accountNumber")
    @Mapping( source = "transfereeAccount", target = "transfereeAccountNumber" , qualifiedByName = "accountNumber")
    public abstract TransactionDto.Info toDto(FinanceTransaction transaction);

    public abstract List<TransactionDto.Info> toDto(List<FinanceTransaction> transaction);

    public abstract FinanceTransaction toEntity(TransactionDto dto);

    @Named("accountHolder")
    public String accountHolder(Account account) {
        if(account == null) return null;
        return account.getAccountHolder();
    }

    @Named("accountNumber")
    public String accountNumber(Account account) {
        if(account == null) return null;
        return account.getAccountNumber();
    }
}
