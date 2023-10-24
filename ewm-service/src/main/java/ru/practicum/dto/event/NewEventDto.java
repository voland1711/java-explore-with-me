package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.config.CommonConstants;
import ru.practicum.dto.location.LocationDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotNull
    @Length(min = 20, max = 2000)
    @JsonProperty(required = true)
    private String annotation;

    @NotNull
    @JsonProperty(value = "category", required = true)
    private Long category;

    @NotNull
    @Length(min = 20, max = 7000)
    @JsonProperty(required = true)
    private String description;

    @NotNull
    @JsonProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime eventDate;

    @NotNull
    @JsonProperty(required = true)
    private LocationDto location;

    @JsonProperty(defaultValue = "false")
    private Boolean paid;

    @PositiveOrZero
    @JsonProperty(defaultValue = "0")
    private Long participantLimit;

    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    @NotNull
    @JsonProperty(required = true)
    @Length(min = 3, max = 120)
    private String title;

}
