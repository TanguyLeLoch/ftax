package com.natu.ftax.ledger.infrastructure


import com.natu.ftax.ledger.domain.LedgerEntry
import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.Date

@Entity
@Table(name = "ledger_entries")
class LedgerEntryEntity(

    @Id
    val id: String,

    val date: Date,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "ledger_book_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var ledgerBook: LedgerBookEntity,

    @ManyToMany(cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "ledger_entry_balance",
        joinColumns = [JoinColumn(name = "ledger_entry_id")],
        inverseJoinColumns = [JoinColumn(name = "balance_id")]
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    val balances: MutableList<BalanceEntity>

) {

    // No-arg constructor required by JPA
    constructor() : this(id = "dum", ledgerBook = LedgerBookEntity("dum"), balances = mutableListOf(), date = Date())

    companion object {
        fun fromDomain(ledgerEntry: LedgerEntry, ledgerBookId: String): LedgerEntryEntity {
            return LedgerEntryEntity(
                id = ledgerEntry.id,
                ledgerBook = LedgerBookEntity(ledgerBookId),
                balances = ledgerEntry.balances.entries.map { BalanceEntity.fromDomain(it.value) }
                    .toMutableList(),
                date = ledgerEntry.date

            )
        }
    }

    fun toDomain(): LedgerEntry {
        val balancesMap = this.balances
            .map { it.toDomain() }
            .associateBy { it.token } // Creates a Map from the list
            .toMutableMap() // Converts the Map to a MutableMap
        return LedgerEntry(id, balancesMap, date)
    }
}