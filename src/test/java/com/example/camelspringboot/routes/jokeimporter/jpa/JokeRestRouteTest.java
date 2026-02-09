package com.example.camelspringboot.routes.jokeimporter.jpa;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class JokeRestRouteTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    JokeRepository jokeRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        jokeRepository.deleteAll();
    }

    @Test
    void restEndpointShouldReturnJokeWhenExists() {
        Joke joke = new Joke();
        joke.setId(2L);
        joke.setType("programming");
        joke.setSetup("Why do programmers prefer dark mode?");
        joke.setPunchline("Because light attracts bugs!");
        jokeRepository.save(joke);

        ResponseEntity<String> response = restTemplate.getForEntity("/api/jokes/2", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("programmers"));
        assertTrue(response.getBody().contains("bugs"));
    }

    @Test
    void restEndpointShouldReturn404WhenJokeNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/jokes/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Joke not found"));
    }
}
