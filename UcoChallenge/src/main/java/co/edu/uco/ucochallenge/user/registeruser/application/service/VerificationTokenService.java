package co.edu.uco.ucochallenge.user.registeruser.application.service;

import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.VerificationTokenEntity;
import co.edu.uco.ucochallenge.secondary.ports.repository.VerificationTokenRepository;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository repository;
    private final CatalogUseCase catalogUseCase;

    public VerificationTokenService(VerificationTokenRepository repository,
                                   CatalogUseCase catalogUseCase) {
        this.repository = repository;
        this.catalogUseCase = catalogUseCase;
    }

    public String generateEmailToken(UUID userId, Duration validity) {
        return createToken(userId, "EMAIL", validity);
    }

    public String generateSmsToken(UUID userId, Duration validity) {
        return createToken(userId, "SMS", validity);
    }

    private String createToken(UUID userId, String type, Duration validity) {
        String token = UUID.randomUUID().toString();
        VerificationTokenEntity entity = new VerificationTokenEntity();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setType(type);
        entity.setExpiresAt(Instant.now().plus(validity));
        repository.save(entity);
        return token;
    }

    public void consumeEmailToken(String token) {
        consume(token, "EMAIL");
    }

    public void consumeSmsToken(String token) {
        consume(token, "SMS");
    }

    private void consume(String token, String expectedType) {
        var entity = repository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException(catalogUseCase.getMessage("token.invalid")));

        if (!entity.getType().equals(expectedType)) {
            throw new IllegalArgumentException(catalogUseCase.getMessage("token.type.incorrect"));
        }
        if (Instant.now().isAfter(entity.getExpiresAt())) {
            throw new IllegalArgumentException(catalogUseCase.getMessage("token.expired"));
        }
        if (entity.isUsed()) {
            throw new IllegalArgumentException(catalogUseCase.getMessage("token.already.used"));
        }

        entity.setUsed(true);
        repository.save(entity);
        System.out.println("[TOKEN] " + expectedType + " confirmado para usuario: " + entity.getUserId());
    }
}


