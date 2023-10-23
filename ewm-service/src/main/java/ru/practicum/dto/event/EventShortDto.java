package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.config.CommonConstants;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventShortDto {
    private Long id;

    @JsonProperty(required = true)
    private String annotation;

    @JsonProperty(required = true)
    private CategoryDto category;

    private Long confirmedRequests;

    @JsonProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime eventDate;

    @JsonProperty(required = true)
    private UserShortDto initiator;

    @JsonProperty(required = true)
    private Boolean paid;

    @JsonProperty(required = true)
    private String title;

    private Long views;

}
