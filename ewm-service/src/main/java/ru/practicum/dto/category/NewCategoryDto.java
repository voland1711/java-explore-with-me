package ru.practicum.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
public class NewCategoryDto {
    @NonNull
    @NotBlank
    @Size(max = 50)
    @JsonProperty(required = true)
    private String name;

}
