package com.natu.ftax.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    private String id;
    @NotNull
    private String ticker;
    @NotNull
    private String name;
    private String externalId;
    private Integer decimals;
    private String logoUrl;

    public Token(String id, String ticker, String name) {
        this.id = id;
        this.ticker = ticker;
        this.name = name;
    }

}
