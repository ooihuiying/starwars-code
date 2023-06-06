package com.example.demo;

import com.example.demo.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/information")
@RequiredArgsConstructor
public class StarshipController {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public StarshipController() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping
    public ResponseEntity<Response> getStarshipInformation() throws IOException {
        StarshipInformation starship = getStarship();
        boolean isLeiaOnPlanet = isLeiaOnAlderaan();

        return ResponseEntity.ok().body(
                Response.builder().starship(starship.getStarship()).crew(starship.getCrewCount()).isLeiaOnPlanet(String.valueOf(isLeiaOnPlanet)).build()
        );
    }

    protected StarshipInformation getStarship() throws IOException {
        String url = "https://swapi.dev/api/starships/?search=Death Star";
        ResponseEntity<Object> response
                = restTemplate.getForEntity(url, Object.class);
        SwapiStarshipResponse swapiStarshipResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiStarshipResponse.class);
        if (swapiStarshipResponse.getResults() == null || swapiStarshipResponse.getResults().size() != 1) {
            return StarshipInformation.builder().crewCount("0").build();
        }
        StarshipResponse starshipResponse = swapiStarshipResponse.getResults().get(0);
        return StarshipInformation
                .builder()
                .crewCount(starshipResponse.getCrew())
                .starship(Starship.builder().starshipClass(starshipResponse.getStarshipClass()).model(starshipResponse.getModel()).name(starshipResponse.getName()).build())
                .build();
    }

    protected boolean isLeiaOnAlderaan() throws JsonProcessingException {

        String planetUrl = getPlanetUrl();
        String url = "https://swapi.dev/api/people/?search=Leia Organa";
        ResponseEntity<Object> response
                = restTemplate.getForEntity(url, Object.class);
        SwapiPersonResponse swapiPersonResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiPersonResponse.class);
        if (swapiPersonResponse.getResults() == null || swapiPersonResponse.getResults().size() != 1) {
            return false;
        }

        return swapiPersonResponse.getResults().get(0).getHomeworld().equals(planetUrl);
    }

    protected String getPlanetUrl() throws JsonProcessingException {
        String url = "https://swapi.dev/api/planets/?search=Alderaan";
        ResponseEntity<Object> response
                = restTemplate.getForEntity(url, Object.class);
        SwapiPlanetResponse swapiResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiPlanetResponse.class);
        if (swapiResponse.getResults() == null || swapiResponse.getResults().size() != 1) {
            return "";
        }

        return swapiResponse.getResults().get(0).getUrl();
    }
}
