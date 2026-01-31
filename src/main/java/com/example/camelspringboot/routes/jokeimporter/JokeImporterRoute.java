package com.example.camelspringboot.routes.jokeimporter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

@Component
public class JokeImporterRoute extends RouteBuilder {

    @Value("${app.jokeimporter.from}")
    private String from;

    @Value("${app.jokeimporter.merge_query}")
    private String mergeQuery;

    @Override
    public void configure() {

        from(from)
            .routeId("joke-importer-route")
            .to("https://official-joke-api.appspot.com/random_joke?httpMethod=GET")
            .unmarshal().json(Jackson, Joke.class)
            .process(exchange -> {
                var joke = exchange.getMessage().getBody(Joke.class);
                var query = String.format(mergeQuery,
                        joke.getId(),
                        joke.getType(),
                        joke.getSetup().replace("'", "''"),
                        joke.getPunchline().replace("'", "''"));
                exchange.getMessage().setBody(query);
            })
            .log("Executing query: ${body}")
            .to("jdbc:datasource");
    }
}