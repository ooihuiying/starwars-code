package com.example.demo;

import com.example.demo.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Controller for retrieving starship information and Leia's location.
 */
@RestController
@RequestMapping("/information")
@RequiredArgsConstructor
@Slf4j
public class StarshipController {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String swapiStarshipSearchUrl;
    private final String swapiPeopleSearchUrl;
    private final String swapiPlanetSearchUrl;

    private final Integer ONE_RESULT = 1;
    private final Integer FIRST_RESULT_INDEX = 0;
    private final String DEATH_START_CONST = "Death Star";
    private final String LEIA_NAME_CONST = "Leia Organa";
    private final String ALDERAAN_PLANET_CONST = "Alderaan";

    /**
     * Retrieves starship information and Leia's location.
     *
     * @return ResponseEntity with the starship information and Leia's location
     */
    @GetMapping
    public ResponseEntity<Response> getStarshipInformation() {
        try {
            StarshipInformation starship = getStarship();
            boolean isLeiaOnPlanet = isLeiaOnAlderaan();

            return ResponseEntity.ok().body(
                    Response.builder().starship(starship.getStarship()).crew(starship.getCrewCount()).isLeiaOnPlanet(String.valueOf(isLeiaOnPlanet)).build()
            );
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(
                    Response.builder().build()
            );
        }
    }

    /**
     * Retrieves starship information from the Star Wars API.
     *
     * @return StarshipInformation object containing the starship details
     * @throws IOException if there is an error parsing the API response
     */
    protected StarshipInformation getStarship() throws IOException {
        String url = swapiStarshipSearchUrl + DEATH_START_CONST;
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        SwapiStarshipResponse swapiStarshipResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiStarshipResponse.class);
        if (swapiStarshipResponse.getResults() == null || swapiStarshipResponse.getResults().size() != ONE_RESULT) {
            return StarshipInformation.builder().build();
        }
        StarshipResponse starshipResponse = swapiStarshipResponse.getResults().get(FIRST_RESULT_INDEX);
        return StarshipInformation
                .builder()
                .crewCount(starshipResponse.getCrew())
                .starship(Starship.builder().starshipClass(starshipResponse.getStarshipClass()).model(starshipResponse.getModel()).name(starshipResponse.getName()).build())
                .build();
    }

    /**
     * Checks if Leia is on the planet Alderaan.
     *
     * @return true if Leia is on Alderaan, false otherwise
     * @throws JsonProcessingException if there is an error parsing the API response
     */
    protected boolean isLeiaOnAlderaan() throws JsonProcessingException {
        String planetUrl = getPlanetUrl();
        String url = swapiPeopleSearchUrl + LEIA_NAME_CONST;
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        SwapiPersonResponse swapiPersonResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiPersonResponse.class);
        if (swapiPersonResponse.getResults() == null || swapiPersonResponse.getResults().size() != ONE_RESULT) {
            return false;
        }

        return swapiPersonResponse.getResults().get(FIRST_RESULT_INDEX).getHomeworld().equals(planetUrl);
    }

    /**
     * Retrieves the URL of the planet Alderaan.
     *
     * @return the URL of Alderaan
     * @throws JsonProcessingException if there is an error parsing the API response
     */
    protected String getPlanetUrl() throws JsonProcessingException {
        String url = swapiPlanetSearchUrl + ALDERAAN_PLANET_CONST;
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        SwapiPlanetResponse swapiResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), SwapiPlanetResponse.class);
        if (swapiResponse.getResults() == null || swapiResponse.getResults().size() != ONE_RESULT) {
            return "";
        }

        return swapiResponse.getResults().get(FIRST_RESULT_INDEX).getUrl();
    }
}
