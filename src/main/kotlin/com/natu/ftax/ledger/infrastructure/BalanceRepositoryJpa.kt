package com.natu.ftax.ledger.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface BalanceRepositoryJpa : JpaRepository<BalanceEntity, String> {

    @Modifying
    @Query(value = "DELETE b FROM balances b " +
            "LEFT JOIN ledger_entry_balance leb ON b.id = leb.balance_id " +
            "WHERE leb.ledger_entry_id IS NULL", nativeQuery = true)
    fun cleanOrphanBalances()
}