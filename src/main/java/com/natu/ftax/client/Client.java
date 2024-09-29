package com.natu.ftax.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
public class Client {

    @Id
    @NotNull
    private String email;
    @Setter
    private String username;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private Profile profile;


    /**
     * default value for profile
     *
     * @return profile
     */
    public Profile getProfile() {
        if (profile == null) {
            profile = new Profile(email, Profile.CalculationMethod.FIFO);
        }
        return profile;
    }


}
