package com.abiz.service;

import com.abiz.exception.handler.AccountNumberBlockException;
import com.abiz.exception.handler.AccountNumberNotFoundException;
import com.abiz.mapper.AccountMapper;
import com.abiz.model.Account;
import com.abiz.model.dto.AccountDto;
import com.abiz.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.abiz.exception.handler.AccountNumberBlockException.TYPE.HAS_ALREADY_BLOCKED;
import static com.abiz.exception.handler.AccountNumberBlockException.TYPE.HAS_ALREADY_UN_BLOCKED;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private AccountMapper mapper;
    @Lazy
    @Autowired
    private FinanceTransactionService transactionService;


    @Transactional
    @Override
    public AccountDto.Info open(AccountDto dto) {
        Integer nextAccountNumber = repository.getNextAccountNumber();
        Account account = mapper.toEntity(dto);
        account.setAccountNumber(String.format("%04d", nextAccountNumber));
        account.setCreateDate(new Date());
        Account saved = repository.save(account);
        transactionService.depositForAccountOpening(saved);
        AccountDto.Info info = mapper.toDto(saved);
        return info;
    }

    @Override
    public Account getByAccountNumber(String accountNumber) {
        Account account = repository.findByAccountNumber(accountNumber).orElseThrow(AccountNumberNotFoundException::new);
        return account;
    }


    @Override
    public List<AccountDto.Info> getList(String user, Integer page, Integer size) {
        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Account> list = repository.findAllByAccountHolder(pageRequest, user);
        return mapper.toDto(list);
    }

    @Transactional
    @Override
    public void block(String accountNumber) {
        Account account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNumberNotFoundException::new);
        if (account.isBlocked()) {
            throw new AccountNumberBlockException(HAS_ALREADY_BLOCKED, accountNumber);
        }
        account.setBlocked(true);
        repository.save(account);
    }

    @Override
    public void block(String accountHolder, String accountNumber) {
        repository.findByAccountHolderAndAccountNumber(accountHolder,accountNumber).orElseThrow(AccountNumberNotFoundException::new);
        block(accountNumber);
    }

    @Transactional
    @Override
    public void unblock(String accountNumber) {
        Account account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNumberNotFoundException::new);
        if (!account.isBlocked()) {
            throw new AccountNumberBlockException(HAS_ALREADY_UN_BLOCKED, accountNumber);
        }
        account.setBlocked(false);
        repository.save(account);
    }

    @Override
    public void unblock(String accountHolder, String accountNumber) {
        repository.findByAccountHolderAndAccountNumber(accountHolder,accountNumber).orElseThrow(AccountNumberNotFoundException::new);
        unblock(accountNumber);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    public void setBalance(String accountNumber, double balance) {
        Account account = repository.findByAccountNumber(accountNumber).orElseThrow();
        account.setBalance(balance);
        repository.save(account);
    }
}
