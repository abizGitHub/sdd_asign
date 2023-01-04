package com.abiz.repository;

import com.abiz.model.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByAccountHolder(Pageable pageable, String accountHolder);

    @Query(value = "call next value for ACCOUNT_NUMBER_SEQUENCE", nativeQuery = true)
    Integer getNextAccountNumber();

    Optional<Account> findByAccountHolderAndAccountNumber(String accountHolder, String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);
}

