package com.natu.ftax.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.natu.ftax.client.Client;
import com.natu.ftax.transaction.calculation.Pnl;
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
public class Transaction {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;
    @NotNull
    private Type type;
    @Column(precision = 64, scale = 30)
    private BigDecimal amount;
    private String token;
    @Column(precision = 64, scale = 30)
    private BigDecimal price;

    private String externalId;
    @NotNull
    private String platform;

    private String address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pnl_id")
    private Pnl pnl;

    @Transient
    @JsonIgnore
    private String errorMessage;

    @Transient
    @JsonIgnore
    private boolean validationPerformed = false;

    @JsonProperty("valid")
    @NotNull
    public boolean isValid() {
        if (!validationPerformed) {
            performValidation();
        }
        return errorMessage == null;
    }

    @JsonProperty("error")
    public String getErrorMessage() {
        if (!validationPerformed) {
            performValidation();
        }
        return errorMessage;
    }

    @JsonIgnore
    private void performValidation() {
        if (localDateTime == null) {
            errorMessage = "Date is invalid";
        } else if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            errorMessage = "Amount is invalid";
        } else if (token == null) {
            errorMessage = "Token is invalid";
        } else if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            errorMessage = "Price is invalid";
        } else if (type == null) {
            errorMessage = "Action is invalid";
        } else if (pnl != null && pnl.getErrorMessage() != null) {
            errorMessage = pnl.getErrorMessage();
        }
        validationPerformed = true;
    }


    public enum Type {
        BUY, SELL
    }

    public void attachPnl(String tokenId, BigDecimal value) {
        this.pnl = new Pnl(this.id, tokenId, value);
    }

    public void attachPnl(String tokenId, String errorMessage) {
        this.pnl = new Pnl(this.id, tokenId, errorMessage);
    }
}
