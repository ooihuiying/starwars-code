package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Starship {
    private String name;
    @JsonProperty("class")
    private String starshipClass;
    private String model;
}
