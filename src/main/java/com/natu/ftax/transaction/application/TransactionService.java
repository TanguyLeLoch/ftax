package com.natu.ftax.transaction.application;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.domain.Transaction;
import com.natu.ftax.transaction.presentation.EditFieldRequest;
import com.natu.ftax.transaction.presentation.SubmitTransactionRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final IdGenerator idGenerator;
    private final TransactionRepository transactionRepository;

    public TransactionService(IdGenerator idGenerator, TransactionRepository transactionRepository) {
        this.idGenerator = idGenerator;
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction() {
        Transaction transaction = Transaction.create(idGenerator.generate());
        transactionRepository.save(transaction);
        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public void submitDraftTransaction(SubmitTransactionRequest request) {
        Transaction transaction = transactionRepository.getTransactionById(request.getId());
        transaction.submit(request.toCommand());
        transactionRepository.save(transaction);
    }

    public Transaction editField(EditFieldRequest request) {
        Transaction transaction = transactionRepository.getTransactionById(request.getId());
        request.toCommand().execute(transaction);
        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction editTransaction(String id) {
        Transaction transaction = transactionRepository.getTransactionById(id);
        transaction.edit();
        transactionRepository.save(transaction);
        return transaction;
    }

    public void deleteTransaction(String id) {
        transactionRepository.deleteTransaction(id);
    }
}
