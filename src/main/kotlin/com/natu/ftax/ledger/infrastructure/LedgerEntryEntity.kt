package com.natu.ftax.ledger.infrastructure



import com.natu.ftax.ledger.domain.LedgerEntry
import jakarta.persistence.*

@Entity
@Table(name = "ledger_entries")
class LedgerEntryEntity(

    @Id
    val id: String,

    @ManyToOne
    @JoinColumn(name = "ledger_book_id")
    var ledgerBook: LedgerBookEntity,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "ledger_entry_balance",
        joinColumns = [JoinColumn(name = "ledger_entry_id")],
        inverseJoinColumns = [JoinColumn(name = "balance_id")]
    )
    val balances: MutableList<BalanceEntity>

){

    // No-arg constructor required by JPA
    protected constructor() : this(id = "dum", ledgerBook = LedgerBookEntity("dum"), balances = mutableListOf())
companion object {
        fun fromDomain(ledgerEntry: LedgerEntry, ledgerBookId : String): LedgerEntryEntity {
            return LedgerEntryEntity(
                id = ledgerEntry.id,
                ledgerBook = LedgerBookEntity(ledgerBookId),
                balances = ledgerEntry.balances.entries.map { BalanceEntity.fromDomain(it.value) }
                    .toMutableList()
            )
        }
    }

    fun toDomain(): LedgerEntry {
        val balancesMap = this.balances
            .map { it.toDomain() }
            .associateBy { it.token } // Creates a Map from the list
            .toMutableMap() // Converts the Map to a MutableMap
        return LedgerEntry(id, balancesMap)
    }
}