package com.example.camelspringboot.routes.jokeimporter.jpa;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class JokeRestRoute extends RouteBuilder {

    private final JokeRepository jokeRepository;

    public JokeRestRoute(JokeRepository jokeRepository) {
        this.jokeRepository = jokeRepository;
    }

    @Override
    public void configure() {
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json);

        rest("/jokes")
            .get("/{id}")
            .to("direct:getJoke");

        from("direct:getJoke")
            .routeId("get-joke-route")
            .process(exchange -> {
                Long id = exchange.getIn().getHeader("id", Long.class);
                var joke = jokeRepository.findById(id).orElse(null);
                if (joke == null) {
                    exchange.getMessage().setHeader("CamelHttpResponseCode", 404);
                    exchange.getMessage().setBody("{\"error\":\"Joke not found\"}");
                } else {
                    exchange.getMessage().setBody(joke);
                }
            });
    }
}
