package com.natu.ftax.transaction.application;

import com.natu.ftax.transaction.domain.Transaction;
import java.util.List;

public interface TransactionRepository {
    Transaction getTransactionById(String id);
    List<Transaction> getAllTransactions();
    void save(Transaction transaction);
    void deleteTransaction(String id);
}
