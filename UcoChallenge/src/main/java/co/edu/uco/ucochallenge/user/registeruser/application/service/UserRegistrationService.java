package co.edu.uco.ucochallenge.user.registeruser.application.service;

import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import co.edu.uco.ucochallenge.user.registeruser.application.interactor.usecase.CatalogUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class UserRegistrationService {

    private final UserRepository repo;
    private final VerificationTokenService tokenService;
    private final NotificationService notification;
    private final CatalogUseCase catalogUseCase;

    public UserRegistrationService(UserRepository repo,
                                   VerificationTokenService tokenService,
                                   NotificationService notification,
                                   CatalogUseCase catalogUseCase) {
        this.repo = repo;
        this.tokenService = tokenService;
        this.notification = notification;
        this.catalogUseCase = catalogUseCase;
    }

    @Transactional
    public UUID register(UserEntity incoming, String ejecutor) {
        if (incoming.getId() == null) {
            incoming.setId(UUID.randomUUID());
        }

        repo.findByEmail(incoming.getEmail()).ifPresent(existing -> {
            notification.notifyActor(ejecutor, catalogUseCase.getMessage("notification.email.duplicate.actor", incoming.getEmail()));
            notification.notifyOwnerEmail(existing.getEmail(), catalogUseCase.getMessage("notification.email.duplicate.owner"));
            throw new BusinessRuleException(catalogUseCase.getMessage("business.email.registered"));
        });

        if (incoming.getMobileNumber() != null) {
            repo.findByMobileNumber(incoming.getMobileNumber()).ifPresent(existing -> {
                notification.notifyActor(ejecutor, catalogUseCase.getMessage("notification.mobile.duplicate.actor", incoming.getMobileNumber()));
                notification.notifyOwnerSms(existing.getMobileNumber(), catalogUseCase.getMessage("notification.mobile.duplicate.owner"));
                throw new BusinessRuleException(catalogUseCase.getMessage("business.mobile.registered"));
            });
        }

        repo.findByIdTypeAndIdNumber(incoming.getIdType(), incoming.getIdNumber()).ifPresent(existing -> {
            notification.notifyActor(ejecutor, catalogUseCase.getMessage("notification.identification.duplicate.actor"));
            notification.notifyAdmin(catalogUseCase.getMessage("notification.identification.duplicate.admin", 
                    incoming.getIdType().toString(), incoming.getIdNumber()));
            throw new BusinessRuleException(catalogUseCase.getMessage("business.identification.registered"));
        });

        UserEntity saved = repo.save(incoming);

        var emailToken = tokenService.generateEmailToken(saved.getId(), Duration.ofMinutes(10));
        notification.sendEmailVerification(saved.getEmail(), emailToken);

        if (saved.getMobileNumber() != null) {
            var smsToken = tokenService.generateSmsToken(saved.getId(), Duration.ofMinutes(10));
            notification.sendSmsVerification(saved.getMobileNumber(), smsToken);
        }

        notification.notifyActor(ejecutor, catalogUseCase.getMessage("notification.user.registered.success"));
        return saved.getId();
    }
    // Confirmacion de Tokens de email y sms
    @Transactional
    public void confirmEmail(String token) {
        tokenService.consumeEmailToken(token);
    }

    @Transactional
    public void confirmSms(String token) {
        tokenService.consumeSmsToken(token);
    }

    public static class BusinessRuleException extends RuntimeException {
        public BusinessRuleException(String msg) { super(msg); }
    }
}

