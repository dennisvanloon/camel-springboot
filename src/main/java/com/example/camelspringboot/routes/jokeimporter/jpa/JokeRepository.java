package com.example.camelspringboot.routes.jokeimporter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
