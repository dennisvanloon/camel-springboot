package com.example.camelspringboot.routes.jmsreader;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class JmsReaderRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("{{app.jmsreader.dlq.uri}}"));

        from("{{app.jmsreader.input.uri}}")
                .routeId("jms-reader-route")
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    if (body != null && body.contains("false")) {
                        throw new IllegalArgumentException("Invalid content");
                    }
                })
                .log("Received message: ${body}");
    }

}
