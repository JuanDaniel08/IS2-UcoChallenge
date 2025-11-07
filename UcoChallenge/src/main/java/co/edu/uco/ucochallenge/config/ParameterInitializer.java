package co.edu.uco.ucochallenge.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.application.service.RedisParameterService;

@Component
public class ParameterInitializer implements CommandLineRunner {

    private final RedisParameterService redisParameterService;

    public ParameterInitializer(RedisParameterService redisParameterService) {
        this.redisParameterService = redisParameterService;
    }

    @Override
    public void run(String... args) throws Exception {
        // ============ PARÁMETROS GLOBALES ============
        redisParameterService.setParameter("app.max_login_attempts", "5");
        redisParameterService.setParameter("app.token_expiration_minutes", "10");
        redisParameterService.setParameter("app.email_verification_timeout", "10");
        redisParameterService.setParameter("app.sms_verification_timeout", "10");
        redisParameterService.setParameter("app.max_users_per_page", "20");

        // ============ MENSAJES DE ERROR ============
        redisParameterService.setErrorMessage("ERR_ID_EXISTS", "El documento de identidad ya está registrado");
        redisParameterService.setErrorMessage("ERR_USER_NOT_FOUND", "El usuario no fue encontrado");
        redisParameterService.setErrorMessage("ERR_CITY_NOT_FOUND", "La ciudad no fue encontrada");
        redisParameterService.setErrorMessage("ERR_ID_TYPE_NOT_FOUND", "El tipo de identificación no fue encontrado");
        redisParameterService.setErrorMessage("validation.user.email.duplicate", "El email ya está registrado");
        redisParameterService.setErrorMessage("validation.user.mobile.duplicate", "El teléfono ya está registrado");
        redisParameterService.setErrorMessage("validation.city.not.exists", "La ciudad no existe");
        redisParameterService.setErrorMessage("validation.user.not.exists", "El usuario no existe");
        // ============ AL CREAR UN USUARIO ============
        redisParameterService.setErrorMessage("validation.user.id.duplicate", "Este Id ya existe");
        redisParameterService.setErrorMessage("ERR_EMAIL_EXISTS", "El email ya está registrado");
        redisParameterService.setErrorMessage("ERR_MOBILE_EXISTS", "El teléfono ya está registrado");
        redisParameterService.setErrorMessage("validation.user.idtype.idnumber.duplicate", "El documento de identidad ya está registrado");


        // ============ MENSAJES DE NOTIFICACIÓN ============
        redisParameterService.setErrorMessage("notification.email.verification.sent", "Se envió un email de verificación");
        redisParameterService.setErrorMessage("notification.sms.verification.sent", "Se envió un SMS de verificación");
        redisParameterService.setErrorMessage("notification.email.duplicate.owner", "Tu email fue utilizado en otro registro");
        redisParameterService.setErrorMessage("notification.mobile.duplicate.owner.sms", "Tu teléfono fue utilizado en otro registro");
        redisParameterService.setErrorMessage("notification.identification.duplicate.admin", "Se detectó un intento de registro duplicado");

        System.out.println("✓ Parámetros y mensajes cargados en Redis exitosamente");
    }
}
