package ru.practicum.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class UserShortDto {
    @JsonProperty(required = true)
    private Long id;

    @JsonProperty(required = true)
    private String name;

}
