package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {
    @Size(min = 10, max = 200)
    @NotBlank
    @NonNull
    @JsonProperty(value = "commentText", required = true)
    private String commentText;
}
