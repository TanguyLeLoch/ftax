package com.natu.ftax.transaction.auth;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.common.exception.TechnicalException;
import com.natu.ftax.transaction.infrastructure.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final IdGenerator idGenerator;
    private final AuthRepo authRepo;
    private final ClientRepo clientRepo;
    private final EmailService emailService;

    public AuthController(
            IdGenerator idGenerator,
            AuthRepo authRepo,
            ClientRepo clientRepo,
            EmailService emailService) {

        this.idGenerator = idGenerator;
        this.authRepo = authRepo;
        this.clientRepo = clientRepo;
        this.emailService = emailService;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @Transactional
    public AuthResponse createHashAndSendMagicLink(@RequestBody Client client) {

        String hash = generateHash(UUID.randomUUID().toString());
        Auth auth = new Auth(idGenerator.generate(), client, hash);

        authRepo.save(auth);

        emailService.sendMagicLink(client.getEmail(), client.getUsername(), generateHash(hash));
        return new AuthResponse(hash);
    }

    @PostMapping("/verify")
    @Transactional
    @ResponseStatus(value = HttpStatus.OK)
    public void verifyHash(@RequestParam("email") String email, @RequestParam("hash") String hash) {
        Client client = clientRepo.findById(email).orElseThrow(() -> new NotFoundException("Invalid email"));
        var auths = authRepo.findByClient(client);

        if (auths.isEmpty()) {
            throw new FunctionalException("Invalid email");
        }

        Auth found = null;
        for (Auth auth : auths) {
            String hashDb = auth.getHash();
            String hashedHash = generateHash(hashDb);
            if (hashedHash.equals(hash)) {
                found = auth;
                break;
            }
        }
        if (found == null) {
            throw new FunctionalException("Invalid hash");
        }
        found.setVerified(true);
        authRepo.save(found);
    }


    public String generateHash(String hash) {
        try {

            String secret = "my super secret";
            String combined = hash + secret;

            // Create SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Compute hash
            byte[] encodedHash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string using your method
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new TechnicalException("No such algorithm", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

