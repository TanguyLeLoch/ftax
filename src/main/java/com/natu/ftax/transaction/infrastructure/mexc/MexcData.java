package com.natu.ftax.transaction.infrastructure.mexc;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.natu.ftax.transaction.domain.EditTransactionCommand;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
public class MexcData {

    @ExcelProperty("Pairs")
    private String pair;

    @ExcelProperty("Time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

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


    public EditTransactionCommand toCommand(String id) {
        String tokenIn;
        String tokenOut;
        boolean buy = side.equals("BUY");
        if (buy) {
            tokenIn = pair.split("_")[1];
            tokenOut = pair.split("_")[0];
        } else {
            tokenIn = pair.split("_")[0];
            tokenOut = pair.split("_")[1];
        }
        return new EditTransactionCommand(
                id,
                "swap",
                time.toInstant(ZoneOffset.UTC),
                tokenIn,
                tokenOut,
                pair.split("_")[0],// TODO check this
                buy ? total : executedAmount,
                buy ? executedAmount : total,
                fee,
                null);

    }
}