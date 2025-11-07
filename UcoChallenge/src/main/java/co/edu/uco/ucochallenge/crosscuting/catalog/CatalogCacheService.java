package co.edu.uco.ucochallenge.crosscuting.catalog;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CatalogCacheService {

    @Cacheable(value = "messages", key = "#messageKey")
    public String getCachedMessage(String messageKey) {
        // Aquí iría la lógica para obtener el mensaje de la BD o recurso
        // Por ahora retorna null para que se cachee cuando se setee
        return null;
    }

    @CacheEvict(value = "messages", key = "#messageKey")
    public void evictMessage(String messageKey) {
        // Limpia el caché de un mensaje específico
    }

    @CacheEvict(value = "messages", allEntries = true)
    public void evictAllMessages() {
        // Limpia todo el caché de mensajes
    }
}
