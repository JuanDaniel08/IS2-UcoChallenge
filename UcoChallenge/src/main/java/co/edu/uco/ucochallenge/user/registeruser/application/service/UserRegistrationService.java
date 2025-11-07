package co.edu.uco.ucochallenge.user.registeruser.application.service;

import co.edu.uco.ucochallenge.application.service.RedisParameterService;
import co.edu.uco.ucochallenge.secondary.ports.repository.UserRepository;
import co.edu.uco.ucochallenge.secondary.adapters.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class UserRegistrationService {

    private final UserRepository repo;
    private final VerificationTokenService tokenService;
    private final NotificationService notification;
    private final RedisParameterService redisParameterService;

    public UserRegistrationService(UserRepository repo,
                                   VerificationTokenService tokenService,
                                   NotificationService notification,
                                   RedisParameterService redisParameterService) {
        this.repo = repo;
        this.tokenService = tokenService;
        this.notification = notification;
        this.redisParameterService = redisParameterService;
    }

    @Transactional
    public UUID register(UserEntity incoming, String ejecutor) {
        if (incoming.getId() == null) {
            incoming.setId(UUID.randomUUID());
        }

        repo.findByEmail(incoming.getEmail()).ifPresent(existing -> {
            String actorMsg = redisParameterService.getErrorMessage("notification.email.duplicate.actor");
            String ownerMsg = redisParameterService.getErrorMessage("notification.email.duplicate.owner");
            notification.notifyActor(ejecutor, actorMsg != null ? actorMsg : "El email ya está registrado");
            notification.notifyOwnerEmail(existing.getEmail(), ownerMsg != null ? ownerMsg : "Tu email fue utilizado");
            String errorMsg = redisParameterService.getErrorMessage("ERR_EMAIL_EXISTS");
            throw new BusinessRuleException(errorMsg != null ? errorMsg : "El email ya está registrado");
        });

        if (incoming.getMobileNumber() != null) {
            repo.findByMobileNumber(incoming.getMobileNumber()).ifPresent(existing -> {
                String actorMsg = redisParameterService.getErrorMessage("notification.mobile.duplicate.actor");
                String ownerMsg = redisParameterService.getErrorMessage("notification.mobile.duplicate.owner.sms");
                notification.notifyActor(ejecutor, actorMsg != null ? actorMsg : "El móvil ya está registrado");
                notification.notifyOwnerSms(existing.getMobileNumber(), ownerMsg != null ? ownerMsg : "Tu móvil fue utilizado");
                String errorMsg = redisParameterService.getErrorMessage("ERR_MOBILE_EXISTS");
                throw new BusinessRuleException(errorMsg != null ? errorMsg : "El móvil ya está registrado");
            });
        }

        repo.findByIdTypeAndIdNumber(incoming.getIdType(), incoming.getIdNumber()).ifPresent(existing -> {
            String actorMsg = redisParameterService.getErrorMessage("notification.identification.duplicate.actor");
            String adminMsg = redisParameterService.getErrorMessage("notification.identification.duplicate.admin");
            notification.notifyActor(ejecutor, actorMsg != null ? actorMsg : "El documento ya está registrado");
            notification.notifyAdmin(adminMsg != null ? adminMsg : "Documento duplicado: " + incoming.getIdNumber());
            String errorMsg = redisParameterService.getErrorMessage("validation.user.idtype.idnumber.duplicate");
            throw new BusinessRuleException(errorMsg != null ? errorMsg : "El documento de identidad ya está registrado");
        });

        UserEntity saved = repo.save(incoming);

        // Obtener timeout de Redis
        String timeoutStr = redisParameterService.getParameter("app.email_verification_timeout");
        int timeout = timeoutStr != null ? Integer.parseInt(timeoutStr) : 10;

        // Generar y enviar token de email
        var emailToken = tokenService.generateEmailToken(saved.getId(), Duration.ofMinutes(timeout));
        String emailMsg = redisParameterService.getErrorMessage("notification.email.verification.sent");
        notification.sendEmailVerification(saved.getEmail(), emailToken, emailMsg);

        // Generar y enviar token de SMS si existe móvil
        if (saved.getMobileNumber() != null) {
            var smsToken = tokenService.generateSmsToken(saved.getId(), Duration.ofMinutes(timeout));
            String smsMsg = redisParameterService.getErrorMessage("notification.sms.verification.sent");
            notification.sendSmsVerification(saved.getMobileNumber(), smsToken, smsMsg);
        }

        String successMsg = redisParameterService.getErrorMessage("notification.user.registered.success");
        notification.notifyActor(ejecutor, successMsg != null ? successMsg : "Usuario registrado exitosamente");
        return saved.getId();
    }

    // Confirmación de Tokens de email y sms
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
