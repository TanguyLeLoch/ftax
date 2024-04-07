package com.natu.ftax.ledger.domain

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.transaction.domain.Token
import com.natu.ftax.transaction.domain.Transaction

class LedgerEntry (
    val id: String,
    val balances: MutableMap<Token, Balance>
) {
    companion object {
        fun create(id: String, previousEntry: LedgerEntry, tx:Transaction, idGenerator: IdGenerator): LedgerEntry {
            val balances = mutableMapOf<Token, Balance>()
            for ((key, value) in previousEntry.balances) {
                balances[key] = Balance(idGenerator.generate(), value.amount, value.token)
            }
            val ledgerEntry = LedgerEntry(id, balances)
            ledgerEntry.adjustBalanceIn(tx , idGenerator.generate())
            ledgerEntry.adjustBalanceOut(tx, idGenerator.generate())
            ledgerEntry.adjustBalanceFee(tx, idGenerator.generate())
            return ledgerEntry
        }

        fun first(id: String): LedgerEntry {
            return LedgerEntry(id, mutableMapOf())
        }
    }


    private fun adjustBalanceIn(transaction: Transaction, balanceId : String) {
        if (transaction.amountIn == 0.0) return
        val tokenIn:Token = transaction.tokenIn!!
        val oldBalanceTokenIn = balances[tokenIn]?.amount ?: 0.0
        val newBalanceTokenIn = oldBalanceTokenIn + transaction.amountIn
        if (newBalanceTokenIn == 0.0) {
            balances.remove(tokenIn)
            return
        }
        balances[tokenIn] = Balance(balanceId, newBalanceTokenIn, tokenIn)
    }
    private fun adjustBalanceOut(transaction: Transaction, balanceId : String) {
        if (transaction.amountOut == 0.0) return
        val tokenOut:Token = transaction.tokenOut!!
        val oldBalanceTokenOut = balances[tokenOut]?.amount ?: 0.0
        val newBalanceTokenOut = oldBalanceTokenOut - transaction.amountOut
        if (newBalanceTokenOut == 0.0) {
            balances.remove(tokenOut)
            return
        }
        balances[tokenOut] = Balance(balanceId, newBalanceTokenOut, tokenOut)
    }

    private fun adjustBalanceFee(transaction: Transaction, balanceId : String) {
        if (transaction.amountFee == 0.0) return
        val tokenFee:Token = transaction.tokenFee!!
        val oldBalanceTokenFee = balances[tokenFee]?.amount ?: 0.0
        val newBalanceTokenFee = oldBalanceTokenFee + transaction.amountFee
        if (newBalanceTokenFee == 0.0) {
            balances.remove(tokenFee)
            return
        }
        balances[tokenFee] = Balance(balanceId, newBalanceTokenFee, tokenFee)
    }

}