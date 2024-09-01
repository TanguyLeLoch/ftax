package com.natu.ftax.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Token {

    @Id
    private String id;
    @NotNull
    private String ticker;
    @NotNull
    private String name;

}
