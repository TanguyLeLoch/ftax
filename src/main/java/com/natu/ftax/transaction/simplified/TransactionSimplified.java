package com.natu.ftax.transaction.simplified;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.natu.ftax.client.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @ManyToOne
    @JoinColumn(name = "email", nullable = false,
            foreignKey = @ForeignKey(name = "FK_TX_CLIENT", foreignKeyDefinition = "FOREIGN KEY (email) REFERENCES client(email) ON DELETE CASCADE;"))
    @JsonIgnore
    private Client client;

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
    private BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pnl_id")
    private Pnl pnl;

    @Transient
    private String errorMessage;

    @NotNull
    @Transient
    public boolean isValid() {
        if (localDateTime == null) {
            errorMessage = "Date is invalid";
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            errorMessage = "Amount is invalid";
            return false;
        }
        if (token == null) {
            errorMessage = "Token is invalid";
            return false;
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            errorMessage = "Price is invalid";
            return false;
        }
        if (type == null) {
            errorMessage = "Action is invalid";
        }
        return true;
    }


    enum Type {
        BUY, SELL
    }
}
