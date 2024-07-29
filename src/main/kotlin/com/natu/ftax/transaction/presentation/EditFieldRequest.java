package com.natu.ftax.transaction.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.natu.ftax.transaction.domain.EditTransactionCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class EditFieldRequest {


    @NotNull(message = "Id cannot be null")
    private String id;

    private String transactionType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Instant instant;

    private String tokenIn;
    private String tokenOut;
    private String tokenFee;

    private BigDecimal amountIn;
    private BigDecimal amountOut;
    private BigDecimal amountFee;

    private String externalId;

    public EditTransactionCommand toCommand() {
        return new EditTransactionCommand(
            id,
            transactionType,
            instant,
            tokenIn,
            tokenOut,
            tokenFee,
            amountIn,
            amountOut,
            amountFee,
            externalId
        );
    }

    // Getters and setters
    // ...
}
