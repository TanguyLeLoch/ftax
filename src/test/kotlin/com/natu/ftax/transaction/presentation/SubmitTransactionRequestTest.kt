package com.natu.ftax.transaction.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.natu.ftax.transaction.domain.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat

class SubmitTransactionRequestTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    @Test
    fun `test Jackson mapping from JSON`() {
        val json = """
            {
                "id": "1",
                "transactionType": "SWAP",
                "date": "2022-01-01T12:00:00.000+0000",
                "token1": "BTC",
                "token2": "ETH",
                "tokenFee": "USD",
                "amount1": 1.0,
                "amount2": 2.0,
                "amountFee": 0.1,
                "externalId": "ext-123"
            }
        """.trimIndent()

        val expectedDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val expectedDate = expectedDateFormat.parse("2022-01-01T12:00:00.000+0000")

        val request: SubmitTransactionRequest = objectMapper.readValue(json)

        assertEquals("1", request.id)
        assertEquals(TransactionType.SWAP, request.transactionType)
        assertEquals(expectedDate, request.date)
        assertEquals("BTC", request.token1)
        assertEquals("ETH", request.token2)
        assertEquals("USD", request.tokenFee)
        assertEquals(1.0, request.amount1)
        assertEquals(2.0, request.amount2)
        assertEquals(0.1, request.amountFee)
        assertEquals("ext-123", request.externalId)
    }

}