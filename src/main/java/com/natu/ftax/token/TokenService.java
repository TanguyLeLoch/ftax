package com.natu.ftax.token;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final TokenRepo tokenRepo;
    private final IdGenerator idGenerator;

    public TokenService(TokenRepo tokenRepo, IdGenerator idGenerator) {
        this.tokenRepo = tokenRepo;
        this.idGenerator = idGenerator;
    }


}
