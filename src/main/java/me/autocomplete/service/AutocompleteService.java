package me.autocomplete.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.autocomplete.model.Completion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutocompleteService {
    private static final Logger logger = LoggerFactory.getLogger(AutocompleteService.class);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AutocompleteService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Completion> getCompletions(String prefix) {
        logger.info("Received autocomplete request for prefix: '{}'", prefix);

        try {
            String json = redisTemplate.opsForValue().get(prefix);

            if (json == null) {
                logger.warn("No completions found for prefix: '{}'", prefix);
                return List.of();
            }

            List<String> suggestions = objectMapper.readValue(json, new TypeReference<>() {});
            List<Completion> completions = new ArrayList<>();
            for (String suggestion : suggestions) {
                completions.add(new Completion(suggestion));
            }

            logger.info("Found {} completions for prefix '{}'", completions.size(), prefix);
            return completions;

        } catch (Exception e) {
            logger.error("Error while fetching completions for prefix '{}': {}", prefix, e.getMessage(), e);
            return List.of();
        }
    }
}
