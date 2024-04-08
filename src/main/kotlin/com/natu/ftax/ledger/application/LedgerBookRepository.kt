import com.natu.ftax.ledger.domain.LedgerBook

interface LedgerBookRepository {
    fun save(ledgerBook: LedgerBook)
    fun get() : MutableList<LedgerBook>
    fun delete(ledgerBookId: String)
}