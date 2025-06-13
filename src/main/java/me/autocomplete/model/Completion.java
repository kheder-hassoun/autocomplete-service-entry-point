package me.autocomplete.model;

public class Completion {
    private String query;
    private int frequency;

    public Completion() {}
    public Completion(String query, int frequency) {
        this.query = query;
        this.frequency = frequency;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}