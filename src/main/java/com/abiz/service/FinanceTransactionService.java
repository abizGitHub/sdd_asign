package com.abiz.service;

import com.abiz.model.Account;
import com.abiz.model.dto.TransactionDto;

import java.util.List;

public interface FinanceTransactionService {

    List<TransactionDto.Info> getList(String user, String accountNumber, Integer page, Integer size);

    TransactionDto.Info transfer(String username, TransactionDto dto);

    void depositForAccountOpening(Account account);
}
