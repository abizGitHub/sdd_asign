package com.abiz.repository;

import com.abiz.model.FinanceTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<FinanceTransaction , Long> {

    List<FinanceTransaction> findAllByTransfereeAccountIdOrTransferorAccountIdOrderByTransferDateDesc(Pageable pageable, long transfereeAccountId, long transferorAccountId);

    @Query(value = "SELECT * FROM FINANCE_TRANSACTION WHERE transferor_Account_ID =:accountId OR transferee_Account_ID =:accountId ORDER BY transfer_Date DESC" , nativeQuery = true)
    List<FinanceTransaction> findAllByAccountId(long accountId);

}
