package com.example.camelspringboot.routes.jokeimporter;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class UserImportRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("timer:userImportTimer?period=60000")
            .routeId("user-import-route")
            .to("https://api.example.com/users?httpMethod=GET")
            .unmarshal().json(JsonLibrary.Jackson, User[].class)
            .split(body())
                .process(exchange -> {
                    User user = exchange.getMessage().getBody(User.class);
                    exchange.getMessage().setBody(
                            String.format("INSERT INTO users (id, name, email) VALUES ('%s', '%s', '%s')", user.getId(), user.getName(), user.getEmail()));
                })
                .to("jdbc:datasource")
            .end();
    }
}