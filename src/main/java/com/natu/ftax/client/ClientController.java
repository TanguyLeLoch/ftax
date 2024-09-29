package com.natu.ftax.client;

import com.natu.ftax.common.exception.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientRepo repo;


    public ClientController(ClientRepo repo) {
        this.repo = repo;
    }

    @GetMapping(value = "/{email}", produces = "application/json")
    public Client getClient(@PathVariable("email") String email) {
        email = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return repo.findById(email)
            .orElseThrow(() -> new NotFoundException("Client not found"));
    }

    @PostMapping(value = "/{email}", produces = "application/json")
    public Client updateClient(
        @PathVariable("email") String email,
        @RequestParam("name") String name,
        @RequestParam("calculationMethod") String calculationMethod) {

        email = email.toLowerCase();

        var client = repo.findById(email)
            .orElseThrow(() -> new NotFoundException("Client not found"));
        if (name != null && !name.isBlank()) {
            client.setUsername(name);
        }
        if (calculationMethod != null && !calculationMethod.isBlank()) {
            var calculationMethodEnum =
                Profile.CalculationMethod.fromString(calculationMethod);

            client.getProfile().setCalculationMethod(calculationMethodEnum);
        }
        return repo.save(client);
    }
}
