package com.natu.ftax.token;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.common.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenRepo tokenRepo;
    private final IdGenerator idGenerator;

    public TokenController(TokenRepo tokenRepo, IdGenerator idGenerator) {
        this.tokenRepo = tokenRepo;
        this.idGenerator = idGenerator;
    }


    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    @Transactional
    public Token post(@RequestBody Token token) {
        if (token.getId() == null) {
            token.setId(idGenerator.generate());
        }
        return tokenRepo.save(token);
    }

    @GetMapping(value = "/{id}",
            produces = "application/json"
    )
    public Token get(@PathVariable("id") String id) {
        return tokenRepo.findById(id).orElseThrow(
                () -> new NotFoundException("Token not found: " + id));
    }

    @GetMapping(
            produces = "application/json"
    )
    public List<Token> getAll() {
        return tokenRepo.findAll();
    }

}
