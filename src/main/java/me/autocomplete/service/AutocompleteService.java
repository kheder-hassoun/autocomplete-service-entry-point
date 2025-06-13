package me.autocomplete.service;

import me.autocomplete.model.Completion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutocompleteService {
    private static final Logger logger = LoggerFactory.getLogger(AutocompleteService.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public AutocompleteService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Completion> getCompletions(String prefix) {
        logger.info("Received autocomplete request for prefix: '{}'", prefix);

        try {
            @SuppressWarnings("unchecked")
            List<Completion> completions = (List<Completion>) redisTemplate.opsForValue().get(prefix);

            if (completions == null) {
                logger.warn("No completions found for prefix: '{}'", prefix);
                return List.of(); // return empty list instead of null
            }

            logger.info("Found {} completions for prefix '{}'", completions.size(), prefix);
            return completions;
        } catch (Exception e) {
            logger.error("Error while fetching completions for prefix '{}': {}", prefix, e.getMessage(), e);
            return List.of();
        }
    }
}