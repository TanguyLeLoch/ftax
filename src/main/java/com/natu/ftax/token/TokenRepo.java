package com.natu.ftax.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepo
        extends JpaRepository<Token, String> {

    Optional<Token> findByTicker(String ticker);
}
