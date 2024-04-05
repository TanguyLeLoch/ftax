package com.natu.ftax.ledger.infrastructure

import jakarta.persistence.*

@Entity
@Table(name = "Ledger_Books")
class LedgerBookEntity {
    @Id
    val id: String? = null
}

