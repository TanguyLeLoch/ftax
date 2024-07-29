package com.natu.ftax.transaction.it;

import com.natu.ftax.transaction.domain.Transaction;
import com.natu.ftax.transaction.presentation.TransactionResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void shouldCreateTx() {
        var response = given()
                .when()
            .post("/transaction/draft");
        System.out.println(response.asString());
        response
            .then()
            .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void shouldSubmitDraftTransaction() {
        String txId = createDraftTransaction();
        var response = given()
                .when()
            .header("Content-Type", "application/json")
                .body("{\"id\": \"" + txId + "\", " +
                        "\"date\": \"2024-02-09T23:38:00.000Z\", " +
                        "\"transactionType\": \"TRANSFER\", " +
                        "\"amountIn\": 123, " +
                        "\"amountOut\": 123, " +
                        "\"amountFee\": 123, " +
                        "\"tokenIn\": \"BTC\", " +
                        "\"tokenOut\": \"ETH\", " +
                        "\"tokenFee\": \"BTC\", " +
                        "\"externalId\": \"0x123\"}")
                .post("/transaction/submit");
        response.then().statusCode(200);
    }

    @Test
    void shouldFailWhenSubmittingNonExistingTx() {
        given()
                .when()
            .header("Content-Type", "application/json")
                .body("{\"id\": \"non-existing-id\", " +
                        "\"transactionType\": \"SWAP\", " +
                        "\"date\": \"2022-01-01T12:00:00.000Z\", " +
                        "\"tokenIn\": \"BTC\", " +
                        "\"tokenOut\": \"ETH\", " +
                        "\"tokenFee\": \"USD\", " +
                        "\"amountIn\": 1.0, " +
                        "\"amountOut\": 2.0, " +
                        "\"amountFee\": 0.1, " +
                        "\"externalId\": \"ext-123\"}")
            .post("/transaction/submit")
            .then()
            .statusCode(404)
                .body("message", equalTo("Transaction not found with id: non-existing-id"));
    }

    @Test
    void shouldEditTransaction() {
        String txId = submittedTransaction();
        given()
                .when()
                .header("Content-Type", "application/json")
                .post("/transaction/edit/" + txId)
                .then()
                .statusCode(200)
                .body("state", equalTo("draft"));
    }

    @Test
    void shouldDeleteTransaction() {
        String txId = submittedTransaction();
        given()
                .when()
                .delete("/transaction/delete/" + txId)
            .then()
                .statusCode(200);

        var txIds = getTxIds();
        assertFalse(txIds.contains(txId));
    }

    @Test
    void shouldGetAllTransactions() {
        String tx1Id = createDraftTransaction();
        String tx2Id = createDraftTransaction();

        var ids = getTxIds();

        assertTrue(ids.containsAll(List.of(tx1Id, tx2Id)), "The returned transactions should contain tx1 and tx2");
    }

    private List<String> getTxIds() {
        Response response = given()
                .when()
                .get("/transaction");
        System.out.println(response.asString());
        return response
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id", String.class);
    }

    private String createDraftTransaction() {
        Response response = given()
                .when()
                .post("/transaction/draft");
        System.out.println(response.asString());
        return response
            .then()
            .statusCode(201)
            .extract()
                .path("id");
    }

    private String submittedTransaction() {
        String txId = createDraftTransaction();
        given()
                .when()
                .header("Content-Type", "application/json")
                .body("{\"id\": \"" + txId + "\", " +
                        "\"date\": \"2024-02-09T23:38:00.000Z\", " +
                        "\"transactionType\": \"TRANSFER\", " +
                        "\"amountIn\": 123, " +
                        "\"amountOut\": 123, " +
                        "\"amountFee\": 123, " +
                        "\"tokenIn\": \"BTC\", " +
                        "\"tokenOut\": \"ETH\", " +
                        "\"tokenFee\": \"BTC\", " +
                        "\"externalId\": \"0x123\"}")
                .post("/transaction/submit");
        return txId;
    }
}