package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.config.RequestStatus;

import java.util.List;

@Value
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    private RequestStatus status;

}