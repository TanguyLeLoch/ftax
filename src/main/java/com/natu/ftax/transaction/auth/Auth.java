package com.natu.ftax.transaction.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
public class Auth {


    @Id
    private String id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email")
    private Client client;

    @Getter
    private String hash;

    @Setter
    private boolean verified;

    Auth(String id, Client client, String hash) {
        this.id = id;
        this.client = client;
        this.hash = hash;
        this.verified = false;
    }


}
