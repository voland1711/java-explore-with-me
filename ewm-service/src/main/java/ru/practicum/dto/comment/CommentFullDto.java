package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.config.CommonConstants;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentFullDto {
    @JsonProperty(required = true)
    private Long id;

    @JsonProperty(required = true)
    private String commentText;

    @JsonProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime createdOn;

    @JsonProperty(required = true)
    private EventShortDto event;

    @JsonProperty(required = true)
    private UserShortDto initiator;

    @JsonProperty(required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DATETIME_FORMAT_TYPE)
    private LocalDateTime editedOn;
}
