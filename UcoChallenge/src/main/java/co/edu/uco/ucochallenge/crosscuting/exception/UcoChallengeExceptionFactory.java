package co.edu.uco.ucochallenge.crosscuting.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.secondary.ports.cache.MessageCatalogCache;

@Component
public class UcoChallengeExceptionFactory {

    private static MessageCatalogCache messageCatalog;

    @Autowired
    public UcoChallengeExceptionFactory(MessageCatalogCache messageCatalog) {
        UcoChallengeExceptionFactory.messageCatalog = messageCatalog;
    }

    public static UcoChallengeException create(String messageKey) {
        String message = messageCatalog.getMessage(messageKey);
        return new UcoChallengeException(message);
    }
}
