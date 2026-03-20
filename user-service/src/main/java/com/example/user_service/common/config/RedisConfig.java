package com.example.user_service.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    //클라우드로 갈 때 문제가 안생기기 위한 변경
    @Value("${spring.data.redis.host}")
    private String host ;

    @Value("${spring.data.redis.port}")
    private int port;
    
    @Bean
    @Qualifier("redisToken")  //여러개의 redis를 사용할 수도 있기 때문에 템플릿이 어떤 redis를 쓸건지 templete을 줘야하는데 이름을 명세할 수 있음
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);

        return new LettuceConnectionFactory(configuration);
    }


    @Bean
    @Qualifier("redisToken")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisToken") RedisConnectionFactory rcf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(rcf);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template ;
    }
}
