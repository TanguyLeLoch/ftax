package com.natu.ftax.transaction.infrastructure;

import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.application.TransactionRepository;
import com.natu.ftax.transaction.domain.Transaction;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionRepositoryJpa transactionRepositoryJpa;

    public TransactionRepositoryImpl(TransactionRepositoryJpa transactionRepositoryJpa) {
        this.transactionRepositoryJpa = transactionRepositoryJpa;
    }

    @Override
    public Transaction getTransactionById(String id) {
        return transactionRepositoryJpa.findById(id)
            .map(TransactionEntity::toDomain)
            .orElseThrow(() -> new NotFoundException("Transaction not found with id: " + id));
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepositoryJpa.findAll().stream()
            .map(TransactionEntity::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void save(Transaction transaction) {
        transactionRepositoryJpa.save(TransactionEntity.fromDomain(transaction));
    }

    @Override
    public void deleteTransaction(String id) {
        transactionRepositoryJpa.deleteById(id);
    }
}
