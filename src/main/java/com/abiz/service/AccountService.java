package com.abiz.service;

import com.abiz.model.Account;
import com.abiz.model.dto.AccountDto;

import java.util.List;

public interface AccountService {

    AccountDto.Info open(AccountDto dto);

    Account getByAccountNumber(String accountNumber);

    List<AccountDto.Info> getList(String user, Integer page, Integer size);

    void block(String accountNumber);
    void block(String accountHolder,String accountNumber);

    void unblock(String accountNumber);
    void unblock(String accountHolder,String accountNumber);

    void setBalance(String accountNumber, double balance);
}
