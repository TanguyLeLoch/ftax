package com.natu.ftax.transaction.importer.mexc;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.natu.ftax.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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


    public Transaction toTransaction(String id) {
        return null;
    }
}