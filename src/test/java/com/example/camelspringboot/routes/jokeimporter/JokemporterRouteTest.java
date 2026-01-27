package com.example.camelspringboot.routes.jokeimporter;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
class JokemporterRouteTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    DataSource dataSource;

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:api")
    MockEndpoint apiMock;

    private final String createTableStament = """
            CREATE TABLE IF NOT EXISTS JOKES (
                id  INT PRIMARY KEY,
                type VARCHAR(128),
                setup VARCHAR(128),
                punchline VARCHAR(128)
            );""";

    @BeforeEach
    void setup() throws Exception {
        dataSource.getConnection().createStatement().execute(createTableStament);
        AdviceWith.adviceWith(camelContext, "joke-importer-route", a -> {
            a.replaceFromWith("direct:start");
            a.weaveByToUri("https://official-joke-api.appspot.com*")
                .replace()
                .to("mock:api");
        });
        camelContext.start();
    }

    @Test
    void shouldFetchUsersAndInsertIntoDatabase() throws Exception {
        String mockApiResponse = """
              {
                "type": "general",
                "setup": "Where do hamburgers go to dance?",
                "punchline":"The meat-ball.",
                "id":285
              }
            """;

        apiMock.whenAnyExchangeReceived(e ->
                e.getMessage().setBody(mockApiResponse)
        );
        apiMock.expectedMessageCount(2);

        // Trigger route
        producerTemplate.sendBody("direct:start", null);
        producerTemplate.sendBody("direct:start", null);

        // Assertions
        MockEndpoint.assertIsSatisfied(camelContext);
        //TODO check db
    }
}

