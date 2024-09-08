package com.natu.ftax.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuthRepo
        extends JpaRepository<Auth, String> {

    List<Auth> findByClientEmail(String email);

    boolean existsByClientEmailAndHashAndVerified(String email, String hash, boolean verified);
}