package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@ToString
public class NewCompilationDto {

    private Set<Long> events;

    @JsonProperty(defaultValue = "false")
    private Boolean pinned;

    @Length(max = 50)
    @NotBlank
    @NonNull
    @JsonProperty(required = true)
    private String title;

}
