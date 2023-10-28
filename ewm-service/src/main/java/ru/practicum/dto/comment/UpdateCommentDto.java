package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateCommentDto {
    @JsonProperty(required = true)
    @NonNull
    private Long id;

    @Size(min = 10, max = 2000)
    @NotBlank
    @NonNull
    @JsonProperty(required = true)
    private String commentText;
}
