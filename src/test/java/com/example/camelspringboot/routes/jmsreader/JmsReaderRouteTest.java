package com.example.camelspringboot.routes.jmsreader;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@CamelSpringBootTest
@SpringBootTest
@MockEndpoints("activemq:*")
public class JmsReaderRouteTest {

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:activemq:queue:DLQ.test.topic")
    MockEndpoint dlq;

    @BeforeEach
    void setup() {
        dlq.reset();
    }

    @Test
    void validMessage_shouldNotGoToDlq() throws Exception {
        dlq.expectedMessageCount(0);
        producerTemplate.sendBody("activemq:topic:test.topic", "this is a valid message");
        dlq.assertIsSatisfied();
    }

    @Test
    void invalidMessage_shouldGoToDlq() throws Exception {
        dlq.expectedMessageCount(1);
        dlq.expectedBodiesReceived("this message contains false");
        producerTemplate.sendBody("activemq:topic:test.topic", "this message contains false");
        dlq.assertIsSatisfied();
    }
}
