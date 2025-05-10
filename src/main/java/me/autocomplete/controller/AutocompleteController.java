package me.autocomplete.controller;

import me.autocomplete.model.Completion;
import me.autocomplete.service.AutocompleteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping("/autocomplete")
    public List<Completion> autocomplete(@RequestParam String prefix) {
        return autocompleteService.getCompletions(prefix);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
