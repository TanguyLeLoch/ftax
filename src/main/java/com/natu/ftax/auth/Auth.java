package com.natu.ftax.auth;

import com.natu.ftax.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
public class Auth {


    @Id
    private String id;

    @Getter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email", nullable = false,
            foreignKey = @ForeignKey(name = "FK_AUTH_CLIENT", foreignKeyDefinition = "FOREIGN KEY (email) REFERENCES client(email) ON DELETE CASCADE;"))
    private Client client;

    @Getter
    private String hash;

    @Setter
    @Getter
    private boolean verified;

    Auth(String id, Client client, String hash) {
        this.id = id;
        this.client = client;
        this.hash = hash;
        this.verified = false;
    }


}
