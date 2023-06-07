package com.example.demo;

import com.example.demo.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarshipControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private StarshipController starshipController;

    @BeforeEach
    void beforeEach() {
        final String baseUrl = "https://swapi.dev/api";
        final String starshipSearchUrl = baseUrl + "/starships/?search=";
        final String peopleSearchUrl = baseUrl + "/people/?search=";
        final String planetSearchUrl = baseUrl + "/planets/?search=";
        this.starshipController = new StarshipController(this.restTemplate, this.objectMapper, starshipSearchUrl, peopleSearchUrl, planetSearchUrl);
    }

    @Test
    void testGetStarshipInformation() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.something.com");
        request.setMethod("GET");
        request.setRequestURI(
                "/information");

        StarshipController spyController = spy(starshipController);
        StarshipInformation info = StarshipInformation
                .builder()
                .starship(Starship.builder().starshipClass("class").model("model").name("name").build())
                .crewCount("2")
                .build();

        doReturn(info).when(spyController).getStarship();
        doReturn(true).when(spyController).isLeiaOnAlderaan();
        final ResponseEntity<Response> response = spyController.getStarshipInformation();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getCrew(), "2");
        assertEquals(response.getBody().getIsLeiaOnPlanet(), "true");
        assertEquals(response.getBody().getStarship(), Starship.builder().starshipClass("class").model("model").name("name").build());
    }

    @Test
    void testGetStarshipInformation_NoStarship() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.something.com");
        request.setMethod("GET");
        request.setRequestURI(
                "/information");

        StarshipController spyController = spy(starshipController);
        StarshipInformation info = StarshipInformation
                .builder()
                .crewCount("0")
                .build();

        doReturn(info).when(spyController).getStarship();
        doReturn(true).when(spyController).isLeiaOnAlderaan();
        final ResponseEntity<Response> response = spyController.getStarshipInformation();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getCrew(), "0");
        assertEquals(response.getBody().getIsLeiaOnPlanet(), "true");
        assertEquals(response.getBody().getStarship(), null);
    }

    @Test
    void testGetStarshipInformation_requestFailure() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.something.com");
        request.setMethod("GET");
        request.setRequestURI(
                "/information");

        StarshipController spyController = spy(starshipController);
        StarshipInformation info = StarshipInformation
                .builder()
                .starship(Starship.builder().starshipClass("class").model("model").name("name").build())
                .crewCount("2")
                .build();

        doThrow(new IOException("Error!!")).when(spyController).getStarship();
        final ResponseEntity<Response> response = spyController.getStarshipInformation();
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getBody().getCrew(), "0");
        assertEquals(response.getBody().getIsLeiaOnPlanet(), "false");
        assertEquals(response.getBody().getStarship(), null);
    }
    @Test
    void testGetStarship() throws IOException {
        // Mock the response for the external API call
        String starshipUrl = "https://swapi.dev/api/starships/?search=Death Star";
        SwapiStarshipResponse starshipResponse = SwapiStarshipResponse.builder().results(List.of(
                StarshipResponse.builder().name("Death Star").starshipClass("Star Destroyer").model("Model").crew("2").build())).build();

        when(restTemplate.getForEntity(starshipUrl, Object.class))
                .thenReturn(new ResponseEntity<>(starshipResponse, HttpStatus.OK));
        when(objectMapper.writeValueAsString(starshipResponse)).thenReturn("planet");
        when(objectMapper.readValue("planet", SwapiStarshipResponse.class))
                .thenReturn(starshipResponse);

        // Perform the test
        StarshipInformation starshipInformation = starshipController.getStarship();

        // Assertions
        assertEquals("2", starshipInformation.getCrewCount());
        assertEquals("Death Star", starshipInformation.getStarship().getName());
        assertEquals("Star Destroyer", starshipInformation.getStarship().getStarshipClass());
        assertEquals("Model", starshipInformation.getStarship().getModel());
    }

    @Test
    void testIsLeiaOnAlderaan() throws IOException {
        // Create test data
        SwapiPersonResponse swapiPersonResponse = SwapiPersonResponse.builder()
                .results(List.of(Person.builder()
                        .homeworld("https://swapi.dev/api/planets/2/")
                        .build()))
                .build();
        SwapiPlanetResponse swapiPlanetResponse = SwapiPlanetResponse.builder()
                .results(List.of(PlanetResponse.builder()
                        .url("https://swapi.dev/api/planets/2/")
                        .build()))
                .build();

        // Mock the responses from the REST API calls
        when(restTemplate.getForEntity("https://swapi.dev/api/planets/?search=Alderaan", Object.class))
                .thenReturn(new ResponseEntity<>(swapiPlanetResponse, HttpStatus.OK));
        when(objectMapper.writeValueAsString(swapiPlanetResponse)).thenReturn("planet");
        when(objectMapper.readValue("planet", SwapiPlanetResponse.class))
                .thenReturn(swapiPlanetResponse);

        when(restTemplate.getForEntity("https://swapi.dev/api/people/?search=Leia Organa", Object.class))
                .thenReturn(new ResponseEntity<>(swapiPersonResponse, HttpStatus.OK));
        when(objectMapper.writeValueAsString(swapiPersonResponse)).thenReturn("");
        when(objectMapper.readValue("", SwapiPersonResponse.class))
                .thenReturn(swapiPersonResponse);

        // Call the method under test
        boolean isLeiaOnAlderaan = starshipController.isLeiaOnAlderaan();

        // Assertion
        assertTrue(isLeiaOnAlderaan);
    }


    @Test
    void testIsLeiaOnAlderaan_False() throws IOException {
        // Create test data
        SwapiPersonResponse swapiPersonResponse = SwapiPersonResponse.builder()
                .results(List.of(Person.builder()
                        .homeworld("https://swapi.dev/api/planets/3/")
                        .build()))
                .build();
        SwapiPlanetResponse swapiPlanetResponse = SwapiPlanetResponse.builder()
                .results(List.of(PlanetResponse.builder()
                        .url("https://swapi.dev/api/planets/2/")
                        .build()))
                .build();

        // Mock the responses from the REST API calls
        when(restTemplate.getForEntity("https://swapi.dev/api/planets/?search=Alderaan", Object.class))
                .thenReturn(new ResponseEntity<>(swapiPlanetResponse, HttpStatus.OK));
        when(objectMapper.writeValueAsString(swapiPlanetResponse)).thenReturn("planet");
        when(objectMapper.readValue("planet", SwapiPlanetResponse.class))
                .thenReturn(swapiPlanetResponse);

        when(restTemplate.getForEntity("https://swapi.dev/api/people/?search=Leia Organa", Object.class))
                .thenReturn(new ResponseEntity<>(swapiPersonResponse, HttpStatus.OK));
        when(objectMapper.writeValueAsString(swapiPersonResponse)).thenReturn("");
        when(objectMapper.readValue("", SwapiPersonResponse.class))
                .thenReturn(swapiPersonResponse);

        // Call the method under test
        boolean isLeiaOnAlderaan = starshipController.isLeiaOnAlderaan();

        // Assertion
        assertFalse(isLeiaOnAlderaan);
    }
}