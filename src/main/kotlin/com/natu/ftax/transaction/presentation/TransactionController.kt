package com.natu.ftax.transaction.presentation

import com.natu.ftax.transaction.domain.Transaction
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("transaction")
class TransactionController {

    @PostMapping
    fun createTransaction(): Transaction {

        return Transaction(
                "1",
                Transaction.TransactionType.TRANSFER,
                Date(),
                "token1",
                "token2",
                "tokenFee",
                100.0,
                100.0,
                10.0
        )
    }
}