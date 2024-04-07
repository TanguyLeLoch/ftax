package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.LedgerBook
import jakarta.persistence.*

@Entity
@Table(name = "Ledger_Books")
class LedgerBookEntity(
    @Id
    val id: String,

    @OneToMany(mappedBy = "ledgerBook", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val ledgerEntries: MutableList<LedgerEntryEntity> = mutableListOf()
) {
    // No-arg constructor required by JPA
    protected constructor() : this(id = "dum", ledgerEntries = mutableListOf())

    companion object {
        fun fromDomain(ledgerBook: LedgerBook): LedgerBookEntity {
            return LedgerBookEntity(
                id = ledgerBook.id,
                ledgerEntries = ledgerBook.ledgerEntries.map { LedgerEntryEntity.fromDomain(it, ledgerBook.id) }
                    .toMutableList()

            )
        }
    }

    fun toDomain(): LedgerBook {
        return LedgerBook(
            id = id,
            ledgerEntries = ledgerEntries.map { it.toDomain() }.toMutableList()
        )
    }
}

