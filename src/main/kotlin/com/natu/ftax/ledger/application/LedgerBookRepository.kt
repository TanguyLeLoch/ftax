import com.natu.ftax.ledger.domain.LedgerBook

interface LedgerBookRepository {
    fun save(ledgerBook: LedgerBook)
    fun get() : LedgerBook?
    fun delete(ledgerBookId: String)
}