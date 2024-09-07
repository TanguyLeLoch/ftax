package com.natu.ftax.transaction.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Client {

    @Id
    @NotNull
    private String email;
    private String username;

}
