package com.natu.ftax.transaction.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuthRepo
        extends JpaRepository<Auth, String> {

    List<Auth> findByClient(Client client);
}