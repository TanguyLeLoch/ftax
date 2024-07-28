package com.natu.ftax.ledger.application;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.ledger.domain.LedgerBook;
import com.natu.ftax.ledger.domain.LedgerEntry;
import com.natu.ftax.ledger.infrastructure.LedgerBookRepository;
import com.natu.ftax.transaction.application.TransactionService;
import com.natu.ftax.transaction.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LedgerBookService {

    private final IdGenerator idGenerator;
    private final LedgerBookRepository ledgerBookRepository;
    private final TransactionService transactionService;

    public LedgerBookService(IdGenerator idGenerator, LedgerBookRepository ledgerBookRepository, TransactionService transactionService) {
        this.idGenerator = idGenerator;
        this.ledgerBookRepository = ledgerBookRepository;
        this.transactionService = transactionService;
    }

    public LedgerBook generateLedgerBook() {
        LedgerBook existing = getLedgerBook();
        if (existing != null) {
            ledgerBookRepository.delete(existing.getId());
        }

        LedgerBook ledgerBook = LedgerBook.create(idGenerator.generate());
        List<Transaction> txs = transactionService.getAllTransactions();
        ledgerBook.getLedgerEntries().add(LedgerEntry.first(idGenerator.generate()));
        for (Transaction tx : txs) {
            ledgerBook.getLedgerEntries().add(
                    LedgerEntry.create(
                            idGenerator.generate(),
                            ledgerBook.getLedgerEntries().get(ledgerBook.getLedgerEntries().size() - 1),
                            tx,
                            idGenerator
                    )
            );
        }
        ledgerBookRepository.save(ledgerBook);
        return ledgerBook;
    }

    public void deleteLedgerBook(String ledgerBookId) {
        ledgerBookRepository.delete(ledgerBookId);
    }

    public LedgerBook getLedgerBook() {
        return ledgerBookRepository.get();
    }
}
