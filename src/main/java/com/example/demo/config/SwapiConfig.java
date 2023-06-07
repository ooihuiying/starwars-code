package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwapiConfig {
    @Value("${swapi.api.url}")
    private String swapiUrl;

    @Bean
    String swapiStarshipSearchUrl() {
        return swapiUrl + "/starships/?search=";
    }

    @Bean
    String swapiPeopleSearchUrl() {
        return swapiUrl + "/people/?search=";
    }

    @Bean
    String swapiPlanetSearchUrl() {
        return swapiUrl + "/planets/?search=";
    }

}
