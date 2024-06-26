package com.natu.ftax.ledger.domain

class LedgerBook(val id: String, val ledgerEntries: MutableList<LedgerEntry> = mutableListOf()) {

    companion object {
        fun create(id: String): LedgerBook {
            return LedgerBook(id)
        }
    }



}
