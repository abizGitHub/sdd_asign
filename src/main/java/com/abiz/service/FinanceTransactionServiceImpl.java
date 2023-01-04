package com.abiz.service;

import com.abiz.exception.handler.AccountBalanceException;
import com.abiz.exception.handler.AccountNumberBlockException;
import com.abiz.exception.handler.InsufficientBalanceException;
import com.abiz.mapper.TransactionMapper;
import com.abiz.model.Account;
import com.abiz.model.FinanceTransaction;
import com.abiz.model.OperationType;
import com.abiz.model.dto.TransactionDto;
import com.abiz.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static com.abiz.exception.handler.AccountNumberBlockException.TYPE.HAS_ALREADY_BLOCKED;

@Service
public class FinanceTransactionServiceImpl implements FinanceTransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionMapper mapper;

    @Override
    public List<TransactionDto.Info> getList(String user, String accountNumber, Integer page, Integer size) {
        Account account = accountService.getByAccountNumber(accountNumber);
        if (!account.getAccountHolder().equals(user)) {
            throw new AccessDeniedException("account doesn't belong to user");
        }
        page = (page == null) ? 0 : page;
        size = (size == null) ? Integer.MAX_VALUE : size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<FinanceTransaction> list = repository.findAllByTransfereeAccountIdOrTransferorAccountIdOrderByTransferDateDesc(pageRequest, account.getId(), account.getId());
        return mapper.toDto(list);
    }

    @Transactional
    @Override
    public TransactionDto.Info transfer(String username, TransactionDto dto) {
        Consumer<Account> checkAccountBlock = account -> {
            if (account.isBlocked())
                throw new AccountNumberBlockException(HAS_ALREADY_BLOCKED, account.getAccountNumber());
        };
        Account transferor = accountService.getByAccountNumber(dto.getTransferorAccountNumber());
        if (!username.equals(transferor.getAccountHolder())) {
            throw new AccessDeniedException("");
        }
        Account transferee = accountService.getByAccountNumber(dto.getTransfereeAccountNumber());
        checkAccountBlock.accept(transferee);
        checkAccountBlock.accept(transferor);
        if (dto.getAmount() > transferor.getBalance()) {
            throw new InsufficientBalanceException();
        }
        FinanceTransaction transaction = mapper.toEntity(dto);
        transaction.setTransferDate(new Date());
        transaction.setOperationType(OperationType.WITHDRAW);
        double transferorNewBalance = transferor.getBalance() - dto.getAmount();
        double transfereeNewBalance = transferee.getBalance() + dto.getAmount();
        accountService.setBalance(transferor.getAccountNumber(), transferorNewBalance);
        accountService.setBalance(transferee.getAccountNumber(), transfereeNewBalance);
        transaction.setTransferorAccount(transferor);
        transaction.setTransfereeAccount(transferee);
        FinanceTransaction saved = repository.save(transaction);
        checkAccountBalancy(transferor.getAccountNumber());
        checkAccountBalancy(transferee.getAccountNumber());
        TransactionDto.Info info = mapper.toDto(saved);
        info.setBalance(transferorNewBalance);
        info.setTransferorAccountHolder(transferor.getAccountHolder());
        info.setTransfereeAccountHolder(transferee.getAccountHolder());
        return info;
    }

    @Override
    public void depositForAccountOpening(Account account) {
        FinanceTransaction transaction = new FinanceTransaction();
        transaction.setAmount(account.getBalance());
        transaction.setTransferDate(new Date());
        transaction.setOperationType(OperationType.DEPOSIT);
        transaction.setTransferorAccount(account);
        transaction.setTransfereeAccount(null);
        transaction.setDescription("account opening");
        repository.save(transaction);
    }

    private void checkAccountBalancy(String accountNumber) {
        Account account = accountService.getByAccountNumber(accountNumber);
        List<FinanceTransaction> transactionList = repository.findAllByAccountId(account.getId());

        BigDecimal deposit = transactionList.stream()
                .filter(transaction -> transaction.getOperationType().equals(OperationType.DEPOSIT))
                .map(transaction -> {
                    BigDecimal amount = new BigDecimal(transaction.getAmount());
                    if (accountNumber.equals(transaction.getTransferorAccount().getAccountNumber())) {
                        return amount;
                    } else {
                        return amount.negate();
                    }
                }).reduce(BigDecimal::add).orElse(new BigDecimal(0));

        BigDecimal withdraw = transactionList.stream()
                .filter(transaction -> transaction.getOperationType().equals(OperationType.WITHDRAW))
                .map(transaction -> {
                    BigDecimal amount = new BigDecimal(transaction.getAmount());
                    if (accountNumber.equals(transaction.getTransferorAccount().getAccountNumber())) {
                        return amount;
                    } else {
                        return amount.negate();
                    }
                }).reduce(BigDecimal::add).orElse(new BigDecimal(0));

        double balanceByTransactions = deposit.add(withdraw.negate()).doubleValue();
        if (account.getBalance() != balanceByTransactions)
            throw new AccountBalanceException(balanceByTransactions - account.getBalance());
    }

}
