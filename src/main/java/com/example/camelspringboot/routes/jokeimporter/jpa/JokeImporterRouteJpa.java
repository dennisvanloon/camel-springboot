package com.example.camelspringboot.routes.jokeimporter.jpa;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

@Component
public class JokeImporterRouteJpa extends RouteBuilder {

    private final JokeRepository jokeRepository;

    @Value("${app.jokeimporter.jpa.from}")
    private String from;

    @Value("${app.jokeimporter.jpa.joke_endpoint}")
    private String jokeEndpoint;

    public JokeImporterRouteJpa(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
    }

    @Override
    public void configure() {
        onException(Exception.class)
            .handled(true)
            .log("Failed to save joke: ${exception.message}")
            .to("file:/tmp/failed-jokes?fileName=joke-${date:now:yyyyMMdd-HHmmss}.json");

        from(from)
            .routeId("joke-importer-route-jpa")
            .to(jokeEndpoint)
            .unmarshal().json(Jackson, Joke.class)
            .bean(jokeRepository, "save");
    }
}