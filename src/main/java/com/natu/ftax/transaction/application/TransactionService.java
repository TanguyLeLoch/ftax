package com.natu.ftax.transaction.application;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.transaction.domain.Transaction;
import com.natu.ftax.transaction.presentation.EditFieldRequest;
import com.natu.ftax.transaction.presentation.SubmitTransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final IdGenerator idGenerator;
    private final TransactionRepository transactionRepository;
    private final Map<String, PlatformImporter> platformImporters;

    public TransactionService(IdGenerator idGenerator,
            TransactionRepository transactionRepository,
            Map<String, PlatformImporter> platformImporters) {
        this.idGenerator = idGenerator;
        this.transactionRepository = transactionRepository;
        this.platformImporters = platformImporters;

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

    public void submitDraftTransaction(String id) {
        Transaction transaction = transactionRepository.getTransactionById(id);
        transaction.submit();
        transactionRepository.save(transaction);
    }

    public void importTransactions(String platform, MultipartFile file) {
        PlatformImporter importer = platformImporters.get(
                platform + "Importer");
        if (importer != null) {
            importer.importTransaction(file);
        } else {
            throw new FunctionalException(
                    "Platform " + platform + " not supported");
        }
    }
}

