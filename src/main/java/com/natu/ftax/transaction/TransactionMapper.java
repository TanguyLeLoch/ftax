package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.token.Token;
import com.natu.ftax.token.TokenRepo;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final TokenRepo tokenRepo;

    public TransactionMapper(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }


    Transaction toTransaction(TransactionRequest request, Client client) {

        Token token = null;
        if (request.getTokenId() != null) {
            token = tokenRepo.findById(request.getTokenId())
                    .orElseThrow(() -> new NotFoundException("Token not found"));
        }
        return Transaction.builder()
                .id(request.getId())
                .client(client)
                .localDateTime(request.getLocalDateTime())
                .type(request.getType())
                .amount(request.getAmount())
                .token(token)
                .price(request.getPrice())
                .externalId(request.getExternalId())
                .platform(request.getPlatform())
                .address(request.getAddress())
                .build();
    }
}
