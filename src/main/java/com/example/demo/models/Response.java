package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class Response {
    @JsonSerialize(nullsUsing = EmptyStarshipSerializer.class)
    private Starship starship;
    private String crew;
    @JsonProperty("isLeiaOnPlanet")
    private String isLeiaOnPlanet;
}