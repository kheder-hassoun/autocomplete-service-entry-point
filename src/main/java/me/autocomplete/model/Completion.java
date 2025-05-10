package me.autocomplete.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Completion {
    private String query;
    private long frequency;
    private String last_updated;
}