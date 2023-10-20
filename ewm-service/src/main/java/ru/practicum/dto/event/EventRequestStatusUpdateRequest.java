package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.config.RequestStatus;

import java.util.List;

@Value
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    @JsonProperty(required = true)
    private List<Long> requestIds;

    @JsonProperty(required = true)
    private RequestStatus status;

}