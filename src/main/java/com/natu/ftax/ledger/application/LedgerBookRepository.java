import com.natu.ftax.ledger.domain.LedgerBook;

public interface LedgerBookRepository {
    void save(LedgerBook ledgerBook);
    LedgerBook get();
    void delete(String ledgerBookId);
}
