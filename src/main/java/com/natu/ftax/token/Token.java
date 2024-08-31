package com.natu.ftax.token;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Token {

    @Id
    private String id;
    private String ticker;
    private String name;
    private String iconId;
    private String iconExt;

}
