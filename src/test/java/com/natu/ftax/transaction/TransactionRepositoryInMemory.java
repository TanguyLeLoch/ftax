package com.natu.ftax.transaction;

import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.application.TransactionRepository;
import com.natu.ftax.transaction.domain.Transaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Repository
public class TransactionRepositoryInMemory implements TransactionRepository {

    private final Map<String, Transaction> transactions = new HashMap<>();

    @Override
    public Transaction getTransactionById(String id) {
        Transaction transaction = transactions.get(id);
        if (transaction == null) {
            throw new NotFoundException("Transaction not found with id: " + id);
        }
        return transaction;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public void save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public void deleteTransaction(String id) {
        transactions.remove(id);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        transactions.forEach(this::save);
    }
}