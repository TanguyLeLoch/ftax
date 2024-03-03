package com.natu.ftax.transaction.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
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
        println("Running integration test")
        given()
            .`when`()
            .post("/transaction/draft")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
    }
}