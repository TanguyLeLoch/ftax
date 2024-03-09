package com.natu.ftax.transaction.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
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
                        "id": "1710002332816-0-1",
                        "date": "2024-02-09T23:38",
                        "transactionType": "TRANSFER",
                        "amount1": 123,
                        "amount2": 123,
                        "amountFee": 123,
                        "token1": "BTC",
                        "token2": "ETH",
                        "tokenFee": "BTC",
                        "externalId": "0x123"
                    }                """.trimIndent()
            )
            .post("/transaction/submit")
        println(response.body.asString())
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
                    "date": "2022-01-01T12:00:00.000",
                    "token1": "BTC",
                    "token2": "ETH",
                    "tokenFee": "USD",
                    "amount1": 1.0,
                    "amount2": 2.0,
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
                equalTo("Draft transaction not found with id: non-existing-id")
            )


    }

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