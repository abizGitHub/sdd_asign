package com.abiz.mapper;

import com.abiz.model.Account;
import com.abiz.model.dto.AccountDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto.Info toDto(Account account);
    List<AccountDto.Info> toDto(List<Account> accounts);

    Account toEntity(AccountDto dto);
}
