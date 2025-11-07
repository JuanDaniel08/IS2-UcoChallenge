package co.edu.uco.ucochallenge.secondary.adapters.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import co.edu.uco.ucochallenge.secondary.ports.cache.MessageCatalogCache;

@Component
public class MessageCatalogCacheAdapter implements MessageCatalogCache {

    private final StringRedisTemplate redisTemplate;

    public MessageCatalogCacheAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getMessage(String key) {
        String message = redisTemplate.opsForValue().get(key);
        if (message == null) {
            message = "Mensaje no encontrado para la clave: " + key;
        }
        return message;
    }
}
