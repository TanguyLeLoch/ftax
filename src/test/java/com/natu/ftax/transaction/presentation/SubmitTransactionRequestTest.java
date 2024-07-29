package com.natu.ftax.transaction.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubmitTransactionRequestTest {


    @Test
    void testJacksonMappingFromJSON() throws Exception {
        String json = """
                    {
                        "id": "1",
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
                """;

        SimpleDateFormat expectedDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Instant expectedDate = Instant.parse("2022-01-01T12:00:00.000Z");

        SubmitTransactionRequest request = new ObjectMapper().readValue(json, SubmitTransactionRequest.class);

        assertEquals("1", request.getId());
        assertEquals("swap", request.getTransactionType());
        assertEquals(expectedDate, request.getInstant());
        assertEquals("BTC", request.getTokenIn());
        assertEquals("ETH", request.getTokenOut());
        assertEquals("USD", request.getTokenFee());
        assertEquals(BigDecimal.valueOf(1.0), request.getAmountIn());
        assertEquals(BigDecimal.valueOf(2.0), request.getAmountOut());
        assertEquals(BigDecimal.valueOf(0.1), request.getAmountFee());
        assertEquals("ext-123", request.getExternalId());
    }
}