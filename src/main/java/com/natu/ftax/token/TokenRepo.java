package com.natu.ftax.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepo
        extends JpaRepository<Token, String> {
}
