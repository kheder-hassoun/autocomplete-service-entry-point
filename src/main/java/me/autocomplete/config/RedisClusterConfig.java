package me.autocomplete.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Configuration
@EnableConfigurationProperties
public class RedisClusterConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.cluster.nodes}") String nodes,
            @Value("${spring.data.redis.password:}")     String password) {

        RedisClusterConfiguration cfg =
                new RedisClusterConfiguration(Arrays.asList(nodes.split("\\s*,\\s*")));
        if (StringUtils.hasText(password)) {
            cfg.setPassword(password);
        }
        return new LettuceConnectionFactory(cfg);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
