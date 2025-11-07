package co.edu.uco.ucochallenge.application.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisParameterService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PARAMETER_PREFIX = "param:";
    private static final String MESSAGE_PREFIX = "msg:";

    public RedisParameterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Guardar parámetro global
    public void setParameter(String key, String value) {
        redisTemplate.opsForValue().set(PARAMETER_PREFIX + key, value);
    }

    // Guardar parámetro con expiración
    public void setParameter(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(PARAMETER_PREFIX + key, value, timeout, unit);
    }

    // Obtener parámetro global
    public String getParameter(String key) {
        Object value = redisTemplate.opsForValue().get(PARAMETER_PREFIX + key);
        return value != null ? value.toString() : null;
    }

    // Eliminar parámetro
    public void deleteParameter(String key) {
        redisTemplate.delete(PARAMETER_PREFIX + key);
    }

    // Verificar si existe un parámetro
    public boolean hasParameter(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PARAMETER_PREFIX + key));
    }

    // Guardar mensaje de error
    public void setErrorMessage(String code, String message) {
        redisTemplate.opsForValue().set(MESSAGE_PREFIX + code, message);
    }

    // Obtener mensaje de error
    public String getErrorMessage(String code) {
        Object value = redisTemplate.opsForValue().get(MESSAGE_PREFIX + code);
        return value != null ? value.toString() : null;
    }

    // Guardar mensaje con expiración
    public void setErrorMessage(String code, String message, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(MESSAGE_PREFIX + code, message, timeout, unit);
    }
}
