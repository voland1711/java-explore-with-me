package ru.practicum.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String email;
}
