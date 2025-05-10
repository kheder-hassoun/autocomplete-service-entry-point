package me.autocomplete.service;

import me.autocomplete.model.Completion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AutocompleteService {

    private final MongoTemplate mongoTemplate;

    @Value("${autocomplete.collection}")
    private String collectionName;

    public AutocompleteService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Completion> getCompletions(String prefix) {
        Query query = new Query(Criteria.where("prefix").is(prefix));
        Map result = mongoTemplate.findOne(query, Map.class, collectionName);

        if (result != null && result.containsKey("completions")) {
            List<Map> list = (List<Map>) result.get("completions");
            return list.stream().map(doc -> {
                Completion c = new Completion();
                c.setQuery((String) doc.get("query"));
                c.setFrequency(((Number) doc.get("frequency")).longValue());
                c.setLast_updated(doc.get("last_updated") != null ?
                        doc.get("last_updated").toString() : null);

                return c;
            }).toList();
        } else {
            return Collections.emptyList();
        }
    }
}