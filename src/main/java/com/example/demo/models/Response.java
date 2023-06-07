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
    @Builder.Default
    @JsonSerialize(nullsUsing = EmptyStarshipSerializer.class)
    private Starship starship=null;
    @Builder.Default
    private String crew = "0";
    @JsonProperty("isLeiaOnPlanet")
    @Builder.Default
    private String isLeiaOnPlanet = "false";
}