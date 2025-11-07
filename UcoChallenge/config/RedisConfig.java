package co.edu.uco.ucochallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import co.edu.uco.ucochallenge.user.registeruser.application.service.listener.NotificationListener;

@Configuration
public class RedisConfig {


    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                // ingresar lo que se va a guardar dentro de la cache
                .fromSerializer(new Jackson2JsonRedisSerializer<>(/* tipo de dato que se va a guardar */ )));
                // builder para crear el cache manager
                return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

  
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            NotificationListener notificationListener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:email:verification"));
        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:sms:verification"));
        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:actor"));
        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:owner:email"));
        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:owner:sms"));
        container.addMessageListener(new MessageListenerAdapter(notificationListener),
                new ChannelTopic("notification:admin"));

        return container;
    }
}

