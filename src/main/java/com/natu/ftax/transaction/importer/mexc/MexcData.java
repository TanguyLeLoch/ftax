package com.natu.ftax.transaction.importer.mexc;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.natu.ftax.client.Client;
import com.natu.ftax.token.Token;
import com.natu.ftax.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

@Getter
@Setter
public class MexcData {

    @ExcelProperty("Pairs")
    private String pair;

    @ExcelProperty("Time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;

    @ExcelProperty("Side")
    private String side;

    @ExcelProperty("Filled Price")
    private BigDecimal filledPrice;

    @ExcelProperty("Executed Amount")
    private BigDecimal executedAmount;

    @ExcelProperty("Total")
    private BigDecimal total;

    @ExcelProperty("Fee")
    private BigDecimal fee;

    @ExcelProperty("Role")
    private String role;


    public Transaction toTransaction(String id, Client client,
        Function<String, Token> getOrCreateToken) {
        Token token = getOrCreateToken.apply(pair);
        return new Transaction(id,
            client,
            localDateTime,
            Transaction.Type.valueOf(side),
            executedAmount,
            token.getId(),
            filledPrice,
            null,
            null,
            false);
    }
}