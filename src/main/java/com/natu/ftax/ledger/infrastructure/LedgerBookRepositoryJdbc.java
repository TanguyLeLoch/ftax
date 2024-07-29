package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerBook;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LedgerBookRepositoryJdbc implements LedgerBookRepository {

    private final LedgerBookRepositoryJpa ledgerBookRepositoryJpa;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final BalanceRepository balanceRepository;

    public LedgerBookRepositoryJdbc(LedgerBookRepositoryJpa ledgerBookRepositoryJpa, LedgerEntryRepository ledgerEntryRepository, BalanceRepository balanceRepository) {
        this.ledgerBookRepositoryJpa = ledgerBookRepositoryJpa;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    @Override
    public void save(LedgerBook ledgerBook) {
        ledgerBook.getLedgerEntries().forEach(entry -> ledgerEntryRepository.save(entry, ledgerBook.getId()));
        balanceRepository.cleanOrphanBalances();
    }

    @Override
    public LedgerBook get() {
        List<LedgerBook> result = ledgerBookRepositoryJpa.findAll().stream().map(LedgerBookEntity::toDomain).toList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void delete(String ledgerBookId) {
        ledgerBookRepositoryJpa.deleteById(ledgerBookId);
    }
}
