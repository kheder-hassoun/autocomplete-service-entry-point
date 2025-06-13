package me.autocomplete.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

@Service
public class AutocompleteService {
    private static final Logger logger = LoggerFactory.getLogger(AutocompleteService.class);
    private final StringRedisTemplate redisTemplate; // Redis values are strings
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AutocompleteService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<String> getCompletions(String prefix) {
        logger.info("Received autocomplete request for prefix: '{}'", prefix);

        try {
            String json = redisTemplate.opsForValue().get(prefix);

            if (json == null) {
                logger.warn("No completions found for prefix: '{}'", prefix);
                return List.of();
            }

            List<String> completions = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            logger.info("Found {} completions for prefix '{}'", completions.size(), prefix);
            return completions;
        } catch (Exception e) {
            logger.error("Error fetching completions for prefix '{}': {}", prefix, e.getMessage(), e);
            return List.of();
        }
    }
}
