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
class UserImportRouteTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    DataSource dataSource;

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:api")
    MockEndpoint apiMock;

    private final String createTableStament = """
            CREATE TABLE IF NOT EXISTS USERS (
                id VARCHAR(16) PRIMARY KEY,
                name VARCHAR(128),
                email VARCHAR(128)
            );""";

    @BeforeEach
    void setup() throws Exception {
        dataSource.getConnection().createStatement().execute(createTableStament);
        AdviceWith.adviceWith(camelContext, "user-import-route", a -> {
            a.replaceFromWith("direct:start");
            a.weaveByToUri("https://api.example.com/*")
                .replace()
                .to("mock:api");
        });
        camelContext.start();
    }

    @Test
    void shouldFetchUsersAndInsertIntoDatabase() throws Exception {
        String mockApiResponse = """
            [
              { "id": 1, "name": "Alice", "email": "alice@test.com" },
              { "id": 2, "name": "Bob", "email": "bob@test.com" }
            ]
            """;

        apiMock.whenAnyExchangeReceived(e ->
                e.getMessage().setBody(mockApiResponse)
        );
        apiMock.expectedMessageCount(1);

        // Trigger route
        producerTemplate.sendBody("direct:start", null);

        // Assertions
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}

