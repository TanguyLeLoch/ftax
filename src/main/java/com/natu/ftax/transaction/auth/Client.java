package com.natu.ftax.transaction.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Client {

    @Id
    private String email;
    private String username;

}
