package com.natu.ftax.transaction.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.natu.ftax.transaction.domain.SubmitTransactionCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
@Getter
public class SubmitTransactionRequest {

    @NotNull(message = "Id cannot be null")
    private String id;

    @NotNull(message = "Transaction type cannot be null")
    private String transactionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Instant date;

    private String tokenIn;
    private String tokenOut;
    private String tokenFee;

    private BigDecimal amountIn;

    private BigDecimal amountOut;

    private BigDecimal amountFee;

    private String externalId;



    public SubmitTransactionCommand toCommand() {
        return new SubmitTransactionCommand(
            id,
            transactionType,
            date,
            tokenIn,
            tokenOut,
            tokenFee,
            amountIn,
            amountOut,
            amountFee,
            externalId
        );
    }

}
