package escuelaing.edu.co.bakend_gl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        log.info("Configurando Redis en {}:{}", redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Board> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Board> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);

        // Usar Jackson para la serialización del valor (Board)
        Jackson2JsonRedisSerializer<Board> serializer = new Jackson2JsonRedisSerializer<>(Board.class);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        
        return template;
    }
} 