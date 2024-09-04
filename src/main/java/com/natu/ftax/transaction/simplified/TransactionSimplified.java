package com.natu.ftax.transaction.simplified;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSimplified {

    @Id
    @NotNull
    private String id;
    @NotNull
    //iso8601 format with milliseconds without timezone
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    @NotNull
    private Type type;
    @Column(precision = 64, scale = 30)
    private BigDecimal amount;
    private String token;
    @Column(precision = 64, scale = 30)
    private BigDecimal dollarValue;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pnl_id")
    private Pnl pnl;

    @NotNull
    @Transient
    public boolean isValid() {
        if (localDateTime == null) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (token == null) {
            return false;
        }
        if (dollarValue == null || dollarValue.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return type != null;
    }

    @NotNull
    @Transient
    @JsonIgnore
    public BigDecimal getPrice() {
        if (dollarValue == null || amount == null) {
            return BigDecimal.ZERO;
        }
        return this.dollarValue.divide(amount, MathContext.DECIMAL64);
    }

    enum Type {
        BUY, SELL
    }
}
