package com.example.camelspringboot.routes.jokeimporter;

public class Joke {
    private Long id;
    private String type;
    private String setup;
    private String punchline;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSetup() {
        return setup;
    }

    public String getPunchline() {
        return punchline;
    }
}
