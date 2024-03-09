package com.natu.ftax.transaction.domain

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

enum class TransactionType {
    TRANSFER, SWAP, NONE;

    companion object {
        @JsonCreator
        @JvmStatic
        fun forValue(value: String): TransactionType =
            TransactionType.valueOf(value.uppercase(Locale.getDefault()))
    }
}
