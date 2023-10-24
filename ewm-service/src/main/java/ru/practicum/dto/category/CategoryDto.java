package ru.practicum.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
@ToString
public class CategoryDto {
    private Long id;

    @Size(max = 50)
    @JsonProperty(required = true)
    private String name;

}
