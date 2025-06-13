package me.autocomplete.controller;

import me.autocomplete.model.Completion;
import me.autocomplete.service.AutocompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AutocompleteController {
    private static final Logger logger = LoggerFactory.getLogger(AutocompleteController.class);
    private final AutocompleteService autocompleteService;

    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping("/autocomplete")
    public List<Completion> autocomplete(@RequestParam String prefix) {
        logger.info("Handling GET /autocomplete for prefix: '{}'", prefix);
        return autocompleteService.getCompletions(prefix);
    }
}