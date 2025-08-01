package me.autocomplete.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.autocomplete.model.Completion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisSystemException;
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

        // normalise
        final String key = (prefix == null) ? "" : prefix.trim().toLowerCase(); // "Maher " → "maher"
        if (key.isEmpty()) {
            logger.warn("Empty prefix after normalisation; returning empty list");
            return List.of();
        }

        try {
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                logger.debug("No completions cached for key '{}'", key);
                return List.of();
            }

            //. deserialise & wrap
            List<String> suggestions = objectMapper.readValue(
                    json, new TypeReference<List<String>>() {});
            List<Completion> completions = new ArrayList<>(suggestions.size());
            for (String suggestion : suggestions) {
                completions.add(new Completion(suggestion));
            }

            logger.info("Found {} completions for normalised key '{}'", completions.size(), key);
            return completions;

        } catch (RedisSystemException e) {
            logger.warn("Redis unavailable for key '{}'; returning empty list", key, e);
            return List.of();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse completions JSON for key " + key, e);
        }
    }

}
