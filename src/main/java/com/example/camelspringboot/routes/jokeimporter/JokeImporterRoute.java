package com.example.camelspringboot.routes.jokeimporter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class JokeImporterRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("timer:jokeImportTimer?period=60000")
            .routeId("joke-importer-route")
            .to("https://official-joke-api.appspot.com/random_joke?httpMethod=GET")
            .unmarshal().json(JsonLibrary.Jackson, Joke.class)
            .process(exchange -> {
                Joke joke = exchange.getMessage().getBody(Joke.class);
                exchange.getMessage().setBody(
                            String.format("MERGE INTO jokes (id, type, setup, punchline) VALUES ('%d', '%s', '%s', '%s')",
                                    joke.getId(), joke.getType(), joke.getSetup(), joke.getPunchline()));
            })
            .to("jdbc:datasource");
    }
}