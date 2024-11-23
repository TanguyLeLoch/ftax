package com.natu.ftax.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.natu.ftax.client.Client;
import com.natu.ftax.token.Token;
import com.natu.ftax.transaction.calculation.Pnl;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Comparable<Transaction> {

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
    private LocalDateTime localDateTime;
    @NotNull
    private Type type;
    @Column(precision = 64, scale = 30)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "token_id")
    @JsonIgnore
    private Token token;

    public String getTokenId() {
        return token != null ? token.getId() : null;
    }

    @Column(precision = 64, scale = 30)
    private BigDecimal price;

    @NotNull
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

    @JsonIgnore
    @Column(name = "valid")
    private boolean valid;

    @PrePersist
    @PreUpdate
    private void updateValidFieldBeforeSave() {
        this.valid = isValid();
        this.validationPerformed = false;
    }

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

    @Override
    public int compareTo(Transaction o) {
        return this.getLocalDateTime().compareTo(o.getLocalDateTime()); // by date asc
    }

    @JsonIgnore
    public boolean hasPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Getter
    public enum Type {
        BUY(BigDecimal.ONE), SELL(BigDecimal.valueOf(-1)), FEE(BigDecimal.valueOf(-1));

        final BigDecimal sign;

        Type(BigDecimal sign) {
            this.sign = sign;
        }

        @JsonValue
        String getValue() {
            return this.name();
        }

        @JsonCreator
        public static Type fromValue(String value) {
            return Type.valueOf(value);
        }
    }

    public void attachErrorPnl(Token token, BigDecimal value) {
        this.pnl = new Pnl(this.id, token, value);
    }

    public void attachErrorPnl(Token token, String errorMessage) {
        this.pnl = new Pnl(this.id, token, errorMessage);
    }
}
