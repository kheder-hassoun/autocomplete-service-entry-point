package me.autocomplete.model;

public class Completion {
    private String query;

    public Completion() {}
    public Completion(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}