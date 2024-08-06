package com.natu.ftax.transaction.infrastructure;

import com.natu.ftax.transaction.domain.Token;
import com.natu.ftax.transaction.domain.Transaction;
import com.natu.ftax.transaction.domain.Value;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(name = "transactions")
public class TransactionEntity {
    private final static int PRECISION = 64;
    private final static int SCALE = 30;

    @Id
    private String id;


    private String state;

    private String transactionType;

    private Instant instant;

    @Column(nullable = true)
    private String tokenIn;
    @Column(nullable = true)
    private String tokenOut;
    @Column(nullable = true)
    private String tokenFee;

    @Column(nullable = true, precision = PRECISION, scale = SCALE)
    private BigDecimal amountOut;
    @Column(nullable = true, precision = PRECISION, scale = SCALE)
    private BigDecimal amountFee ;
    @Column(nullable = true, precision = PRECISION, scale = SCALE)
    private BigDecimal amountIn;

    private String externalId;

    public TransactionEntity() {
    }

    public TransactionEntity(String id, String state, String transactionType,
            Instant instant, String tokenIn, String tokenOut, String tokenFee,
            BigDecimal amountIn, BigDecimal amountOut, BigDecimal amountFee,
            String externalId) {
        this.id = id;
        this.state = state;
        this.transactionType = transactionType;
        this.instant = instant;
        this.tokenIn = tokenIn;
        this.tokenOut = tokenOut;
        this.tokenFee = tokenFee;
        this.amountIn = amountIn;
        this.amountOut = amountOut;
        this.amountFee = amountFee;
        this.externalId = externalId;
    }

    public static TransactionEntity fromDomain(Transaction transaction) {

        return new TransactionEntity(
            transaction.getId(),
            transaction.getState(),
            transaction.getTransactionType(),
            transaction.getInstant(),
            transaction.getSymbolIn(),
            transaction.getSymbolOut(),
            transaction.getSymbolFee(),
            transaction.getAmountIn(),
            transaction.getAmountOut(),
            transaction.getAmountFee(),
            transaction.getExternalId()
        );
    }

    public Transaction toDomain() {
        return Transaction.reconstitute(
                id,
                state,
                transactionType,
                instant,
                tokenIn != null && amountIn != null ?
                        new Value(new Token(tokenIn), amountIn) : null,
                tokenOut != null && amountOut != null ?
                        new Value(new Token(tokenOut), amountOut) : null,
                tokenFee != null && amountFee != null ?
                        new Value(new Token(tokenFee), amountFee) : null,
                externalId
        );
    }

}
