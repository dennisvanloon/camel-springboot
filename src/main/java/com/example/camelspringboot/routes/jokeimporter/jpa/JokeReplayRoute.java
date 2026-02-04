package com.example.camelspringboot.routes.jokeimporter.jpa;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

@Component
public class JokeReplayRoute extends RouteBuilder {

    private final JokeRepository jokeRepository;

    public JokeReplayRoute(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
    }

    @Override
    public void configure() {
        from("file:/tmp/failed-jokes?delete=true")
            .routeId("joke-replay-route")
            .unmarshal().json(Jackson, Joke.class)
            .bean(jokeRepository, "save")
            .log("Replayed joke: ${body.id}");
    }
}
