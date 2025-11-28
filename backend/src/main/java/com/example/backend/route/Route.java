package com.example.backend.route;

public class Route {
    private final long id;
    private final String content;

    public Route(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}