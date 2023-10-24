package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.config.CommonConstants;
import ru.practicum.config.EventState;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class EventFullDto {
    private Long id;

    @JsonProperty(required = true)
    private String annotation;

    @JsonProperty(required = true)
    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime createdOn;

    private String description;

    @JsonProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime eventDate;

    @JsonProperty(required = true)
    private UserShortDto initiator;

    @JsonProperty(required = true)
    private LocationDto location;

    @JsonProperty(required = true)
    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime publishedOn;

    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    private EventState state;

    @JsonProperty(required = true)
    private String title;

    private Long views;

}
