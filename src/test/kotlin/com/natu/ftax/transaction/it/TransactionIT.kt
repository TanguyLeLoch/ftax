package com.natu.ftax.transaction.it

import com.natu.ftax.transaction.domain.Transaction
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionIT {
    @LocalServerPort
    private val port = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `should create a tx`() {
        given()
            .`when`()
            .post("/transaction/draft")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
    }

    @Test
    fun `should submit a draft transaction`() {
        val txId = createDraftTransaction()
        val response = given()
            .`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                    {
                        "id": "$txId",
                        "date": "2024-02-09T23:38:00.000Z",
                        "transactionType": "TRANSFER",
                        "amountIn": 123,
                        "amountOut": 123,
                        "amountFee": 123,
                        "tokenIn": "BTC",
                        "tokenOut": "ETH",
                        "tokenFee": "BTC",
                        "externalId": "0x123"
                    }                """.trimIndent()
            )
            .post("/transaction/submit")
        response
            .then()
            .statusCode(200)
    }

    @Test
    fun `should fail when submitting a non existing tx`() {
        given()
            .`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                {
                    "id": "non-existing-id",
                    "transactionType": "SWAP",
                    "date": "2022-01-01T12:00:00.000Z",
                    "tokenIn": "BTC",
                    "tokenOut": "ETH",
                    "tokenFee": "USD",
                    "amountIn": 1.0,
                    "amountOut": 2.0,
                    "amountFee": 0.1,
                    "externalId": "ext-123"
                }
                """.trimIndent()
            )
            .post("/transaction/submit")
            .then()
            .statusCode(404)
            .body(
                "message",
                equalTo("Transaction not found with id: non-existing-id")
            )
    }

    @Test
    fun `should edit a transaction`() {
        val txId = submittedTransaction()
        given()
            .`when`()
            .header("Content-Type", "application/json")
            .post("/transaction/edit/${txId}")
            .then()
            .statusCode(200)
            .body("state", equalTo("DRAFT"))

    }

    @Test
    fun `should delete a transaction`() {
        val txId = submittedTransaction()
        given()
            .`when`()
            .delete("/transaction/delete/${txId}")
            .then()
            .statusCode(200)

        val txIds = getTxIds()
        assertFalse(txIds.contains(txId))
    }

    private fun submittedTransaction(): String {
        val txId = createDraftTransaction()
        given()
            .`when`()
            .header("Content-Type", "application/json")
            .body(
                """
                        {
                            "id": "$txId",
                            "date": "2024-02-09T23:38:00.000Z",
                            "transactionType": "TRANSFER",
                            "amountIn": 123,
                            "amountOut": 123,
                            "amountFee": 123,
                            "tokenIn": "BTC",
                            "tokenOut": "ETH",
                            "tokenFee": "BTC",
                            "externalId": "0x123"
                        }                """.trimIndent()
            )
            .post("/transaction/submit")
        return txId
    }

    @Test
    fun `should get all transactions`() {
        val tx1Id = createDraftTransaction()
        val tx2Id = createDraftTransaction()

        val ids = getTxIds()

        assertTrue(ids.containsAll(listOf(tx1Id, tx2Id)), "The returned transactions should contain tx1 and tx2")
    }

    private fun getTxIds() = given()
        .`when`()
        .get("/transaction")
        .then()
        .statusCode(200)
        .extract()
        .jsonPath()
        .getList("$", Transaction::class.java)
        .map { it.id }


    private fun createDraftTransaction(): String {
        return given()
            .`when`()
            .post("/transaction/draft")
            .then()
            .statusCode(201)
            .extract()
            .path("id")
    }

}